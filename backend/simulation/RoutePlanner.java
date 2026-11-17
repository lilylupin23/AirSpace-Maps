package backend.simulation;

import backend.models.*;

import java.util.*;
import java.time.Instant;
import java.time.Duration;

public class RoutePlanner {
private static final double EARTH_RADIUS = 6371.0;
private static final int ALTITUDE_STEP = 1000;
private static final int TIME_STEP = 60;
private static final int MAX_SEARCH_TIME = 14400;

private static final double DEFAULT_CLIMB_RATE = 5.0;
private static final double HORIZONTAL_LIMIT = 9.26;
private static final double VERTICAL_LIMIT = 1000.0;

private static final double FEET_TO_METERS = 0.3048;

private record State(
String waypointId,
int altitude,
int time
) {}

private record Node(
State state,
double cost,
double priority
) {}

public List<RoutePoint> findRoute(
String aircraftId,
String startId,
String goalId,
int startAltitude,
int goalAltitude,
Instant departureTime,
double cruiseSpeed,
List<Waypoint> waypoints,
List<WeatherZone> weather,
List<RestrictedZone> restricted,
Collection<AircraftState> traffic) {

if (cruiseSpeed <= 0 || departureTime == null) {
return List.of();
}

Map<String, Waypoint> map = new HashMap<>();

for (Waypoint w : waypoints) {
map.put(w.waypointId(), w);
}

if (!map.containsKey(startId) || !map.containsKey(goalId)) {
return List.of();
}

Waypoint start = map.get(startId);
Waypoint goal = map.get(goalId);

if (!validAltitude(start, startAltitude)
|| !validAltitude(goal, goalAltitude)) {
return List.of();
}

if (isBlocked(start, startAltitude, weather, restricted)
|| isBlocked(goal, goalAltitude, weather, restricted)) {
return List.of();
}

State startState = new State(startId, startAltitude, 0);
State goalState = new State(goalId, goalAltitude, -1);

TreeSet<Integer> altitudeLevels = new TreeSet<>();

int minAltitude = Integer.MAX_VALUE;
int maxAltitude = Integer.MIN_VALUE;

for (Waypoint w : waypoints) {
minAltitude = Math.min(minAltitude, w.minAltitude());
maxAltitude = Math.max(maxAltitude, w.maxAltitude());
}

int firstLevel = (int) Math.ceil(
minAltitude / (double) ALTITUDE_STEP
) * ALTITUDE_STEP;

for (int altitude = firstLevel;
altitude <= maxAltitude;
altitude += ALTITUDE_STEP) {
altitudeLevels.add(altitude);
}

altitudeLevels.add(startAltitude);
altitudeLevels.add(goalAltitude);

List<Integer> levels = new ArrayList<>(altitudeLevels);

PriorityQueue<Node> open = new PriorityQueue<>(
Comparator.comparingDouble(Node::priority)
);

Map<State, Double> costs = new HashMap<>();
Map<State, State> previous = new HashMap<>();

costs.put(startState, 0.0);

open.add(new Node(
startState,
0,
heuristic(start, startAltitude, goal, goalAltitude, cruiseSpeed)
));

while (!open.isEmpty()) {
Node currentNode = open.poll();
State currentState = currentNode.state();

if (currentNode.cost() > costs.getOrDefault(
currentState, Double.MAX_VALUE)) {
continue;
}

if (currentState.waypointId().equals(goalId)
&& currentState.altitude() == goalAltitude) {
return reconstructPath(previous, currentState, map, departureTime);
}

if (currentState.time() >= MAX_SEARCH_TIME) continue;

Waypoint current = map.get(currentState.waypointId());

List<State> neighbours = new ArrayList<>();

// 1. Horizontal movement
for (String nextId : current.connectedWaypointIds()) {
Waypoint next = map.get(nextId);

if (next == null) continue;

if (validAltitude(next, currentState.altitude())) {
int duration = travelTime(
distance(current, next) * 1000.0,
cruiseSpeed
);

neighbours.add(new State(
nextId,
currentState.altitude(),
currentState.time() + duration
));
}
}

// 2. Vertical movement
int index = levels.indexOf(currentState.altitude());

if (index > 0) {
int newAltitude = levels.get(index - 1);

if (validAltitude(current, newAltitude)) {
int duration = travelTime(
Math.abs(newAltitude - currentState.altitude())
* FEET_TO_METERS,
DEFAULT_CLIMB_RATE
);

neighbours.add(new State(
currentState.waypointId(),
newAltitude,
currentState.time() + duration
));
}
}

if (index >= 0 && index < levels.size() - 1) {
int newAltitude = levels.get(index + 1);

if (validAltitude(current, newAltitude)) {
int duration = travelTime(
Math.abs(newAltitude - currentState.altitude())
* FEET_TO_METERS,
DEFAULT_CLIMB_RATE
);

neighbours.add(new State(
currentState.waypointId(),
newAltitude,
currentState.time() + duration
));
}
}

// 3. Waiting at the current waypoint
neighbours.add(new State(
currentState.waypointId(),
currentState.altitude(),
currentState.time() + TIME_STEP
));

for (State nextState : neighbours) {
if (nextState.time() > MAX_SEARCH_TIME) continue;

Waypoint nextWaypoint = map.get(nextState.waypointId());

if (nextWaypoint == null) continue;

if (!validAltitude(nextWaypoint, nextState.altitude())) continue;

if (isBlocked(nextWaypoint, nextState.altitude(), weather, restricted)) {
continue;
}

if (hasTrafficConflict(
current,
currentState.altitude(),
currentState.time(),
nextWaypoint,
nextState.altitude(),
nextState.time(),
departureTime,
aircraftId,
traffic)) {
continue;
}

double movementCost = nextState.time() - currentState.time();

movementCost += weatherPenalty(
nextWaypoint,
nextState.altitude(),
weather
);

double newCost = currentNode.cost() + movementCost;

if (newCost < costs.getOrDefault(nextState, Double.MAX_VALUE)) {
costs.put(nextState, newCost);
previous.put(nextState, currentState);

double priority = newCost + heuristic(
nextWaypoint,
nextState.altitude(),
goal,
goalAltitude,
cruiseSpeed
);

open.add(new Node(nextState, newCost, priority));
}
}
}

return List.of();
}

private int travelTime(double distance, double speed) {
double seconds = distance / speed;

return Math.max(
TIME_STEP,
(int) Math.ceil(seconds / TIME_STEP) * TIME_STEP
);
}

private boolean validAltitude(Waypoint w, int altitude) {
return altitude >= w.minAltitude()
&& altitude <= w.maxAltitude();
}

private boolean isBlocked(
Waypoint w,
int altitude,
List<WeatherZone> weather,
List<RestrictedZone> restricted) {

for (RestrictedZone z : restricted) {
if (!z.active()) continue;

if (altitude >= z.minAltitude()
&& altitude <= z.maxAltitude()
&& distance(w.latitude(), w.longitude(),
z.latitude(), z.longitude()) <= z.radius()) {
return true;
}
}

for (WeatherZone z : weather) {
if (!z.active()) continue;

if ((z.severity().equalsIgnoreCase("severe")
|| z.severity().equalsIgnoreCase("extreme"))
&& altitude >= z.minAltitude()
&& altitude <= z.maxAltitude()
&& distance(w.latitude(), w.longitude(),
z.latitude(), z.longitude()) <= z.radius()) {
return true;
}
}

return false;
}

private double weatherPenalty(
Waypoint w,
int altitude,
List<WeatherZone> weather) {

double penalty = 0;

for (WeatherZone z : weather) {
if (!z.active()) continue;

if (altitude >= z.minAltitude()
&& altitude <= z.maxAltitude()
&& distance(w.latitude(), w.longitude(),
z.latitude(), z.longitude()) <= z.radius()) {

if (z.severity().equalsIgnoreCase("moderate")) {
penalty += 120;
} else if (z.severity().equalsIgnoreCase("high")) {
penalty += 600;
}
}
}

return penalty;
}

private boolean hasTrafficConflict(
Waypoint from,
int fromAltitude,
int fromTime,
Waypoint to,
int toAltitude,
int toTime,
Instant departureTime,
String aircraftId,
Collection<AircraftState> traffic) {

if (traffic == null || traffic.isEmpty()) return false;

int samples = 10;

for (int i = 0; i <= samples; i++) {
double fraction = i / (double) samples;

double lat = from.latitude()
+ (to.latitude() - from.latitude()) * fraction;

double lon = from.longitude()
+ (to.longitude() - from.longitude()) * fraction;

double altitude = fromAltitude
+ (toAltitude - fromAltitude) * fraction;

int time = (int) Math.round(
fromTime + (toTime - fromTime) * fraction
);

for (AircraftState other : traffic) {
if (other.getAircraftId().equals(aircraftId)) continue;

long elapsed = Duration.between(
other.getLastUpdated(), departureTime
).getSeconds();

int predictionTime = (int) Math.max(0, elapsed + time);

double[] predicted = predict(other, predictionTime);

double horizontal = distance(
lat, lon,
predicted[0], predicted[1]
);

double otherAltitudeFeet =
other.getAltitude() / FEET_TO_METERS;

double vertical = Math.abs(
altitude - otherAltitudeFeet
);

if (horizontal <= HORIZONTAL_LIMIT
&& vertical <= VERTICAL_LIMIT) {
return true;
}
}
}

return false;
}

private double[] predict(AircraftState aircraft, int seconds) {
double distanceMeters = aircraft.getVelocity() * seconds;

double bearing = Math.toRadians(aircraft.getHeading());
double lat1 = Math.toRadians(aircraft.getLatitude());
double lon1 = Math.toRadians(aircraft.getLongitude());

double angularDistance = distanceMeters / (EARTH_RADIUS * 1000);

double lat2 = Math.asin(
Math.sin(lat1) * Math.cos(angularDistance)
+ Math.cos(lat1) * Math.sin(angularDistance) * Math.cos(bearing)
);

double lon2 = lon1 + Math.atan2(
Math.sin(bearing) * Math.sin(angularDistance) * Math.cos(lat1),
Math.cos(angularDistance) - Math.sin(lat1) * Math.sin(lat2)
);

return new double[]{
Math.toDegrees(lat2),
Math.toDegrees(lon2)
};
}

private double heuristic(
Waypoint current,
int currentAltitude,
Waypoint goal,
int goalAltitude,
double speed) {

double horizontal = distance(current, goal) * 1000 / speed;

double vertical = Math.abs(goalAltitude - currentAltitude)
* FEET_TO_METERS / DEFAULT_CLIMB_RATE;

return Math.max(horizontal, vertical);
}

private List<RoutePoint> reconstructPath(
Map<State, State> previous,
State goal,
Map<String, Waypoint> map,
Instant departureTime) {

List<RoutePoint> path = new ArrayList<>();
State current = goal;

while (current != null) {
path.add(new RoutePoint(
map.get(current.waypointId()),
current.altitude(),
departureTime.plusSeconds(current.time())
));

current = previous.get(current);
}

Collections.reverse(path);
return path;
}

private double distance(Waypoint a, Waypoint b) {
return distance(
a.latitude(), a.longitude(),
b.latitude(), b.longitude()
);
}

private double distance(
double lat1,
double lon1,
double lat2,
double lon2) {

double dLat = Math.toRadians(lat2 - lat1);
double dLon = Math.toRadians(lon2 - lon1);

double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
+ Math.cos(Math.toRadians(lat1))
* Math.cos(Math.toRadians(lat2))
* Math.sin(dLon / 2) * Math.sin(dLon / 2);

return EARTH_RADIUS * 2
* Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
}
}
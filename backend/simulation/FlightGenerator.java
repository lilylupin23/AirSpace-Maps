package backend.simulation;

import backend.models.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class FlightGenerator {

private static final AtomicInteger aircraftCounter = new AtomicInteger(1);
private static final AtomicInteger flightCounter = new AtomicInteger(1);

public GeneratedTraffic generateFlights(
int count,
List<Aircraft> aircraftCatalog,
List<Airport> airports,
List<Waypoint> waypoints) {

if (count <= 0) throw new IllegalArgumentException("Flight count must be positive");
if (aircraftCatalog.isEmpty() || airports.size() < 2 || waypoints.size() < 2) {
throw new IllegalArgumentException("Insufficient reference data");
}

List<Aircraft> generatedAircraft = new ArrayList<>();
List<Flight> generatedFlights = new ArrayList<>();

for (int i = 0; i < count; i++) {

Airport departure = airports.get(randomIndex(airports.size()));
Airport arrival;

do {
arrival = airports.get(randomIndex(airports.size()));
} while (departure.airportId().equals(arrival.airportId()));

Waypoint start = nearestWaypoint(departure, waypoints);
Waypoint end = nearestWaypoint(arrival, waypoints);

List<String> route = findRoute(start.waypointId(), end.waypointId(), waypoints);

if (route.isEmpty()) {
i--;
continue;
}

Aircraft template = aircraftCatalog.get(randomIndex(aircraftCatalog.size()));

int aircraftNumber = aircraftCounter.getAndIncrement();
int flightNumber = flightCounter.getAndIncrement();

String aircraftId = String.format("SIM-A%04d", aircraftNumber);
String flightId = String.format("SIM-F%04d", flightNumber);
String callsign = String.format("ASM%04d", flightNumber);

Aircraft aircraft = new Aircraft(
aircraftId,
"SIM-REG-" + aircraftNumber,
template.model(),
template.category(),
template.cruiseSpeed(),
template.serviceCeiling(),
template.capacity()
);

int maximumAltitude = Math.min(template.serviceCeiling(), 39000);
int cruiseAltitude = ThreadLocalRandom.current().nextInt(18000, maximumAltitude + 1);

Flight flight = new Flight(
flightId,
callsign,
aircraftId,
departure.icaoCode(),
arrival.icaoCode(),
route,
Instant.now().minusSeconds(ThreadLocalRandom.current().nextLong(300, 3601)),
cruiseAltitude,
"AIRBORNE",
randomPriority()
);

generatedAircraft.add(aircraft);
generatedFlights.add(flight);
}

return new GeneratedTraffic(generatedAircraft, generatedFlights);
}

private int randomIndex(int size) {
return ThreadLocalRandom.current().nextInt(size);
}

private String randomPriority() {
String[] priorities = {"NORMAL", "NORMAL", "NORMAL", "HIGH", "LOW"};
return priorities[randomIndex(priorities.length)];
}

private Waypoint nearestWaypoint(Airport airport, List<Waypoint> waypoints) {

Waypoint nearest = null;
double minimumDistance = Double.MAX_VALUE;

for (Waypoint waypoint : waypoints) {

double distance = distance(
airport.latitude(),
airport.longitude(),
waypoint.latitude(),
waypoint.longitude()
);

if (distance < minimumDistance) {
minimumDistance = distance;
nearest = waypoint;
}
}

return nearest;
}

private List<String> findRoute(
String start,
String destination,
List<Waypoint> waypoints) {

Map<String, Waypoint> graph = new HashMap<>();

for (Waypoint waypoint : waypoints) {
graph.put(waypoint.waypointId(), waypoint);
}

Queue<String> queue = new LinkedList<>();
Map<String, String> previous = new HashMap<>();
Set<String> visited = new HashSet<>();

queue.add(start);
visited.add(start);

while (!queue.isEmpty()) {

String current = queue.poll();

if (current.equals(destination)) break;

Waypoint waypoint = graph.get(current);

if (waypoint == null) continue;

for (String next : waypoint.connectedWaypointIds()) {

if (!graph.containsKey(next) || visited.contains(next)) continue;

visited.add(next);
previous.put(next, current);
queue.add(next);
}
}

if (!visited.contains(destination)) return Collections.emptyList();

List<String> route = new ArrayList<>();
String current = destination;

while (current != null) {
route.add(current);
current = previous.get(current);
}

Collections.reverse(route);
return route;
}

private double distance(double lat1, double lon1, double lat2, double lon2) {

double earthRadius = 6371.0;

double dLat = Math.toRadians(lat2 - lat1);
double dLon = Math.toRadians(lon2 - lon1);

double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
+ Math.cos(Math.toRadians(lat1))
* Math.cos(Math.toRadians(lat2))
* Math.sin(dLon / 2) * Math.sin(dLon / 2);

return earthRadius * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
}
}
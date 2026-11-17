package backend.simulation;

import backend.models.*;
import java.util.*;

public class SimulatedAircraftFactory {

public List<String> createAircraft(
SimulationState simulation,
GeneratedTraffic traffic,
List<Waypoint> waypoints) {

Map<String, Aircraft> aircraftMap = new HashMap<>();
Map<String, Waypoint> waypointMap = new HashMap<>();

for (Aircraft aircraft : traffic.aircraft()) {
aircraftMap.put(aircraft.aircraftId(), aircraft);
}

for (Waypoint waypoint : waypoints) {
waypointMap.put(waypoint.waypointId(), waypoint);
}

List<String> ids = new ArrayList<>();

for (Flight flight : traffic.flights()) {

Aircraft aircraft = aircraftMap.get(flight.aircraftId());

if (aircraft == null || flight.route().isEmpty()) continue;

Waypoint start = waypointMap.get(flight.route().get(0));

if (start == null) continue;

double heading = 0;

if (flight.route().size() > 1) {

Waypoint next = waypointMap.get(flight.route().get(1));

if (next != null) {
heading = calculateHeading(
start.latitude(),
start.longitude(),
next.latitude(),
next.longitude()
);
}
}

AircraftState state = new AircraftState(
aircraft.aircraftId(),
start.latitude(),
start.longitude(),
flight.cruiseAltitude() * 0.3048,
aircraft.cruiseSpeed(),
heading
);

simulation.addSimulatedAircraft(state);
ids.add(aircraft.aircraftId());
}

return ids;
}

private double calculateHeading(
double lat1,
double lon1,
double lat2,
double lon2) {

double dLon = Math.toRadians(lon2 - lon1);

double y = Math.sin(dLon) * Math.cos(Math.toRadians(lat2));

double x = Math.cos(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2))
- Math.sin(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(dLon);

return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
}
}
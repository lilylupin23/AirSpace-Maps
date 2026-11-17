
package backend.simulation;

import backend.models.AircraftState;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FlightSimulation {
private static final double EARTH_RADIUS = 6371000.0;

private final SimulationState simulation;
private final Set<String> simulatedAircraft;
private final Map<String, Instant> lastUpdate = new ConcurrentHashMap<>();

public FlightSimulation(
SimulationState simulation,
Collection<String> simulatedAircraft) {

this.simulation = simulation;
this.simulatedAircraft = ConcurrentHashMap.newKeySet();
this.simulatedAircraft.addAll(simulatedAircraft);
}

public void updateAll() {
Instant now = Instant.now();

for (String id : simulatedAircraft) {
AircraftState aircraft = simulation.getAircraft(id);

if (aircraft == null) continue;

Instant previous = lastUpdate.put(id, now);

if (previous == null) continue;

double seconds = Math.max(
0,
Math.min(60, java.time.Duration.between(previous, now).toMillis() / 1000.0)
);

if (seconds == 0) continue;

if ("HOLDING".equalsIgnoreCase(aircraft.getStatus())) continue;

double distance = aircraft.getVelocity() * seconds;
double angularDistance = distance / EARTH_RADIUS;

double bearing = Math.toRadians(aircraft.getHeading());
double lat1 = Math.toRadians(aircraft.getLatitude());
double lon1 = Math.toRadians(aircraft.getLongitude());

double lat2 = Math.asin(
Math.sin(lat1) * Math.cos(angularDistance)
+ Math.cos(lat1) * Math.sin(angularDistance) * Math.cos(bearing)
);

double lon2 = lon1 + Math.atan2(
Math.sin(bearing) * Math.sin(angularDistance) * Math.cos(lat1),
Math.cos(angularDistance) - Math.sin(lat1) * Math.sin(lat2)
);

aircraft.updatePosition(
Math.toDegrees(lat2),
Math.toDegrees(lon2),
aircraft.getAltitude(),
aircraft.getVelocity(),
aircraft.getHeading()
);
}
}

public void removeAircraft(String id) {
simulatedAircraft.remove(id);
lastUpdate.remove(id);
}
}

package backend.simulation;

import backend.models.Runway;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AirportOperationsService {
private final Map<String, List<Runway>> runways = new ConcurrentHashMap<>();

public void addRunway(Runway runway) {
runways.computeIfAbsent(runway.getAirportId(), k -> new ArrayList<>()).add(runway);
}

public synchronized String allocateRunway(String airportId, String flightId) {
List<Runway> airportRunways = runways.get(airportId);

if (airportRunways == null) return null;

for (Runway runway : airportRunways) {
if (runway.assignFlight(flightId)) {
return runway.getRunwayId();
}
}

return null;
}

public synchronized boolean releaseRunway(String airportId, String runwayId) {
List<Runway> airportRunways = runways.get(airportId);

if (airportRunways == null) return false;

for (Runway runway : airportRunways) {
if (runway.getRunwayId().equals(runwayId)) {
runway.release();
return true;
}
}

return false;
}

public List<Runway> getRunways(String airportId) {
return runways.getOrDefault(airportId, Collections.emptyList());
}
}
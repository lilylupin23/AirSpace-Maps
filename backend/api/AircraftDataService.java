package backend.api;

import backend.models.AircraftState;
import backend.simulation.SimulationState;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.*;

public class AircraftDataService {

private final OpenSkyAPI api;
private final SimulationState simulation;
private final DataProcessor processor;

public AircraftDataService(SimulationState simulation) {
this.api = new OpenSkyAPI();
this.simulation = simulation;
this.processor = new DataProcessor();
}

public void refreshAircraft() throws Exception {

JsonNode response = api.fetchAircraft();

List<AircraftState> processed =
processor.processOpenSkyResponse(response);

Set<String> activeIds = new HashSet<>();

for (AircraftState aircraft : processed) {

activeIds.add(aircraft.getAircraftId());

AircraftState existing =
simulation.getAircraft(aircraft.getAircraftId());

if (existing == null) {
simulation.addLiveAircraft(aircraft);
} else {
existing.updatePosition(
aircraft.getLatitude(),
aircraft.getLongitude(),
aircraft.getAltitude(),
aircraft.getVelocity(),
aircraft.getHeading(),
aircraft.getLastUpdated()
);
}
}

for (AircraftState existing : simulation.getLiveAircraftStates()) {

if (!activeIds.contains(existing.getAircraftId())) {
simulation.removeLiveAircraft(existing.getAircraftId());
}
}

System.out.println("Live aircraft processed: " + processed.size());
}
}
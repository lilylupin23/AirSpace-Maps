package backend.simulation;

import backend.models.AircraftState;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TrafficManager {

private final SimulationState simulation;
private final Map<String, String> instructions = new ConcurrentHashMap<>();

public TrafficManager(SimulationState simulation) {
this.simulation = simulation;
}

public boolean holdAircraft(String id) {

if (!simulation.getSimulatedAircraftIds().contains(id)) return false;

AircraftState aircraft = simulation.getAircraft(id);

if (aircraft == null) return false;

aircraft.setStatus("HOLDING");
instructions.put(id, "HOLD");

return true;
}

public boolean resumeAircraft(String id) {

if (!simulation.getSimulatedAircraftIds().contains(id)) return false;

AircraftState aircraft = simulation.getAircraft(id);

if (aircraft == null) return false;

aircraft.setStatus("AIRBORNE");
instructions.put(id, "RESUME");

return true;
}

public String getInstruction(String id) {
return instructions.getOrDefault(id, "NO INSTRUCTION");
}

public Map<String, String> getAllInstructions() {
return new HashMap<>(instructions);
}
}
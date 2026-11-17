package backend.simulation;

import backend.models.AircraftState;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SimulationState {

private final Map<String, AircraftState> aircraft = new ConcurrentHashMap<>();
private final Set<String> liveAircraft = ConcurrentHashMap.newKeySet();
private final Set<String> simulatedAircraft = ConcurrentHashMap.newKeySet();

public void addAircraft(AircraftState state) {
addLiveAircraft(state);
}

public void addLiveAircraft(AircraftState state) {
aircraft.put(state.getAircraftId(), state);
liveAircraft.add(state.getAircraftId());
simulatedAircraft.remove(state.getAircraftId());
}

public void addSimulatedAircraft(AircraftState state) {
aircraft.put(state.getAircraftId(), state);
simulatedAircraft.add(state.getAircraftId());
liveAircraft.remove(state.getAircraftId());
}

public AircraftState getAircraft(String id) {
return aircraft.get(id);
}

public Collection<AircraftState> getAircraftStates() {
return Collections.unmodifiableCollection(aircraft.values());
}

public Collection<AircraftState> getLiveAircraftStates() {
List<AircraftState> result = new ArrayList<>();

for (String id : liveAircraft) {
AircraftState state = aircraft.get(id);
if (state != null) result.add(state);
}

return result;
}

public Collection<AircraftState> getSimulatedAircraftStates() {
List<AircraftState> result = new ArrayList<>();

for (String id : simulatedAircraft) {
AircraftState state = aircraft.get(id);
if (state != null) result.add(state);
}

return result;
}

public Set<String> getSimulatedAircraftIds() {
return new HashSet<>(simulatedAircraft);
}

public void removeAircraft(String id) {
aircraft.remove(id);
liveAircraft.remove(id);
simulatedAircraft.remove(id);
}

public void removeLiveAircraft(String id) {
if (liveAircraft.remove(id)) {
aircraft.remove(id);
}
}

public int getAircraftCount() {
return aircraft.size();
}

public int getLiveAircraftCount() {
return liveAircraft.size();
}

public int getSimulatedAircraftCount() {
return simulatedAircraft.size();
}
}
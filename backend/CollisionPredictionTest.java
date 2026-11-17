package backend;

import backend.models.AircraftState;
import backend.simulation.*;
import java.util.List;

public class CollisionPredictionTest {

public static void main(String[] args) {

SimulationState simulation = new SimulationState();

AircraftState a1 = new AircraftState(
"TEST-A1", 28.5562, 77.1000, 10000, 200, 90
);

AircraftState a2 = new AircraftState(
"TEST-A2", 28.5562, 77.1000, 10200, 200, 270
);

simulation.addSimulatedAircraft(a1);
simulation.addSimulatedAircraft(a2);

CollisionPredictionService service = new CollisionPredictionService();

List<CollisionAlert> alerts = service.detectCollisions(simulation);

System.out.println("=== COLLISION PREDICTION TEST ===");
System.out.println("Aircraft tested: 2");
System.out.println("Collision alerts: " + alerts.size());

for (CollisionAlert alert : alerts) {
System.out.println("Aircraft: " + alert.aircraftOne() + " and " + alert.aircraftTwo());
System.out.println("Time to conflict: " + alert.secondsToConflict() + " seconds");
System.out.println("Horizontal distance: " + alert.horizontalDistanceKm() + " km");
System.out.println("Vertical separation: " + alert.verticalSeparationMeters() + " m");
}

if (!alerts.isEmpty()) {
System.out.println("PASS: Potential collision detected.");
} else {
System.out.println("FAIL: Collision was not detected.");
}
}
}
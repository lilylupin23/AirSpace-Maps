package backend;

import backend.models.AircraftState;
import backend.simulation.*;
import backend.api.*;

public class AircraftIntegrationTest {

public static void main(String[] args) throws Exception {

System.out.println("=== AIRCRAFT INTEGRATION TEST ===");

SimulationState simulation = new SimulationState();

// Add a simulated aircraft
AircraftState simulated = new AircraftState(
"SIM-PRESERVE-01", 28.5, 77.1, 10000, 200, 90
);

simulation.addSimulatedAircraft(simulated);

double initialLatitude = simulated.getLatitude();
double initialLongitude = simulated.getLongitude();

// Refresh live aircraft from OpenSky
AircraftDataService service = new AircraftDataService(simulation);

System.out.println("\nRefreshing live aircraft data...");
service.refreshAircraft();

// Check simulated aircraft preservation
AircraftState preserved = simulation.getAircraft("SIM-PRESERVE-01");

System.out.println("\n=== RESULTS ===");
System.out.println("Live aircraft: " + simulation.getLiveAircraftStates().size());
System.out.println("Simulated aircraft: " + simulation.getSimulatedAircraftStates().size());

if (preserved != null
&& preserved.getLatitude() == initialLatitude
&& preserved.getLongitude() == initialLongitude
&& simulation.getSimulatedAircraftIds().contains("SIM-PRESERVE-01")) {

System.out.println("PASS: Simulated aircraft preserved.");
} else {
System.out.println("FAIL: Simulated aircraft was modified or removed.");
}
}
}
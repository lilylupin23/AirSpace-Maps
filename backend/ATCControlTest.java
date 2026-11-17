package backend;

import backend.models.AircraftState;
import backend.simulation.*;

public class ATCControlTest {

public static void main(String[] args) {

SimulationState simulation = new SimulationState();

AircraftState simulated = new AircraftState(
"SIM-TEST-01", 28.5, 77.1, 10000, 200, 90
);

AircraftState live = new AircraftState(
"LIVE-TEST-01", 19.0, 72.8, 11000, 210, 180
);

simulation.addSimulatedAircraft(simulated);
simulation.addLiveAircraft(live);

TrafficManager manager = new TrafficManager(simulation);
ATCService atc = new ATCService(manager);

System.out.println("=== ATC CONTROL TEST ===");

// Test HOLD
System.out.println("\nTEST 1: HOLD");
System.out.println(atc.issueHold("SIM-TEST-01"));
System.out.println("Status: " + simulated.getStatus());

// Test RESUME
System.out.println("\nTEST 2: RESUME");
System.out.println(atc.issueResume("SIM-TEST-01"));
System.out.println("Status: " + simulated.getStatus());

// Test live aircraft protection
System.out.println("\nTEST 3: LIVE AIRCRAFT PROTECTION");
System.out.println(atc.issueHold("LIVE-TEST-01"));
System.out.println("Live aircraft status: " + live.getStatus());

// Final checks
if (simulated.getStatus().equals("AIRBORNE")
&& live.getStatus().equals("AIRBORNE")
&& atc.getLastInstruction("SIM-TEST-01").equals("RESUME")
&& atc.getLastInstruction("LIVE-TEST-01").equals("NO INSTRUCTION")) {
System.out.println("\nPASS: ATC controls working correctly.");
} else {
System.out.println("\nFAIL: ATC control test failed.");
}
}
}
package backend;

import backend.simulation.*;

public class AircraftUpdateServiceTest {

public static void main(String[] args) throws InterruptedException {

System.out.println("=== AIRCRAFT UPDATE SERVICE TEST ===");

SimulationState simulation = new SimulationState();

AircraftUpdateService service =
new AircraftUpdateService(simulation);

System.out.println("Starting automatic aircraft updates...");

service.start();

// Allow the initial scheduled execution to begin
Thread.sleep(10000);

System.out.println("Live aircraft detected: "
+ simulation.getLiveAircraftStates().size());

System.out.println("Stopping update service...");

service.stop();

System.out.println("PASS: Update service started and stopped.");
}
}
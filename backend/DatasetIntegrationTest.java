package backend;

import backend.simulation.*;
import backend.models.*;
import java.util.*;

public class DatasetIntegrationTest {

public static void main(String[] args) {
try {

System.out.println("=== DATASET INTEGRATION TEST ===");

SimulationState simulation = new SimulationState();
TrafficGenerationService service = new TrafficGenerationService(simulation);

// TEST 1: Flight generation
System.out.println("\nTEST 1: Flight Generation");

GeneratedTraffic traffic = service.initialize(20);

System.out.println("Flights generated: " + traffic.flights().size());
System.out.println("Aircraft generated: " + traffic.aircraft().size());
System.out.println("Aircraft in simulation: " + simulation.getSimulatedAircraftStates().size());

// TEST 2: Weather integration
System.out.println("\nTEST 2: Weather Integration");

List<WeatherZone> weather = service.refreshWeather();

System.out.println("Weather zones loaded: " + weather.size());

for (WeatherZone zone : weather) {
System.out.println(zone.weatherZoneId() + " - " + zone.weatherType() + " - " + zone.severity());
}

// TEST 3: Simulation update
System.out.println("\nTEST 3: Simulation Update");

service.updateSimulation();

System.out.println("Simulation update completed.");

// TEST 4: Simulated aircraft preservation
System.out.println("\nTEST 4: Aircraft Preservation");

int before = simulation.getSimulatedAircraftStates().size();

service.updateSimulation();

int after = simulation.getSimulatedAircraftStates().size();

System.out.println("Aircraft before update: " + before);
System.out.println("Aircraft after update: " + after);

if (before == after) {
System.out.println("PASS: Simulated aircraft preserved.");
} else {
System.out.println("FAIL: Aircraft count changed.");
}

System.out.println("\n=== INTEGRATION TEST COMPLETED ===");

} catch (Exception e) {
System.out.println("INTEGRATION TEST FAILED");
e.printStackTrace();
}
}
}
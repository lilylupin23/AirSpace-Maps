package backend;

import backend.api.DataLoader;
import backend.models.*;
import backend.simulation.*;
import java.util.*;

public class FlightSimulationTest {

public static void main(String[] args) throws Exception {

DataLoader loader = new DataLoader();
SimulationState simulation = new SimulationState();

List<Aircraft> aircraft = loader.loadAircraft();
List<Airport> airports = loader.loadAirports();
List<Waypoint> waypoints = loader.loadWaypoints();

FlightGenerator generator = new FlightGenerator();

GeneratedTraffic traffic = generator.generateFlights(
5, aircraft, airports, waypoints
);

SimulatedAircraftFactory factory = new SimulatedAircraftFactory();

List<String> ids = factory.createAircraft(
simulation, traffic, waypoints
);

FlightSimulation flightSimulation =
new FlightSimulation(simulation, ids);

System.out.println("=== FLIGHT SIMULATION TEST ===");

AircraftState state = simulation.getAircraft(ids.get(0));

double initialLat = state.getLatitude();
double initialLon = state.getLongitude();

System.out.println("Aircraft: " + state.getAircraftId());
System.out.println("Initial position: " + initialLat + ", " + initialLon);

flightSimulation.updateAll();
Thread.sleep(2000);
flightSimulation.updateAll();

double finalLat = state.getLatitude();
double finalLon = state.getLongitude();

System.out.println("Final position: " + finalLat + ", " + finalLon);

if (initialLat != finalLat || initialLon != finalLon) {
System.out.println("PASS: Aircraft movement detected.");
} else {
System.out.println("FAIL: Aircraft did not move.");
}
}
}
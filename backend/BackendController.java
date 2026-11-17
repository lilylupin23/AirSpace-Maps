package backend;

import backend.api.*;
import backend.models.*;
import backend.simulation.*;
import java.util.*;

public class BackendController {

private final DataLoader loader;
private final SimulationState simulation;
private final CollisionPredictionService collisionService;
private final TrafficManager trafficManager;
private final ATCService atcService;
private final TrafficGenerationService trafficService;

private AircraftUpdateService aircraftUpdateService;

private List<Aircraft> aircraft;
private List<Airport> airports;
private List<Flight> flights;
private List<Waypoint> waypoints;
private List<WeatherZone> weather;
private List<RestrictedZone> restrictedZones;

public BackendController() {
loader = new DataLoader();
simulation = new SimulationState();
collisionService = new CollisionPredictionService();
trafficManager = new TrafficManager(simulation);
atcService = new ATCService(trafficManager);
trafficService = new TrafficGenerationService(simulation);
}

public void initialize() throws Exception {

System.out.println("Loading datasets...");

aircraft = loader.loadAircraft();
airports = loader.loadAirports();
flights = loader.loadFlights();
waypoints = loader.loadWaypoints();
weather = loader.loadWeather();
restrictedZones = loader.loadRestrictedZones();

DataValidator validator = new DataValidator();

List<String> errors = validator.validateAll(
aircraft,
airports,
flights,
waypoints,
weather,
restrictedZones
);

if (!errors.isEmpty()) {
throw new IllegalStateException("Dataset validation failed: " + errors);
}

System.out.println("Dataset validation successful.");

trafficService.initialize(20);
weather = trafficService.refreshWeather();

aircraftUpdateService = new AircraftUpdateService(simulation);
aircraftUpdateService.start();

System.out.println("Backend initialized successfully.");
}

public List<CollisionAlert> getCollisionAlerts() {
return collisionService.detectCollisions(simulation);
}

public String holdAircraft(String aircraftId) {
return atcService.issueHold(aircraftId);
}

public String resumeAircraft(String aircraftId) {
return atcService.issueResume(aircraftId);
}

public String getLastInstruction(String aircraftId) {
return atcService.getLastInstruction(aircraftId);
}

public void updateSimulation() {
trafficService.updateSimulation();
}

public SimulationState getSimulation() {
return simulation;
}

public List<Aircraft> getAircraft() {
return aircraft;
}

public List<Airport> getAirports() {
return airports;
}

public List<Flight> getFlights() {
return flights;
}

public List<Waypoint> getWaypoints() {
return waypoints;
}

public List<WeatherZone> getWeather() {
return weather;
}

public List<RestrictedZone> getRestrictedZones() {
return restrictedZones;
}

public GeneratedTraffic getGeneratedTraffic() {
return trafficService.getGeneratedTraffic();
}

public void shutdown() {
if (aircraftUpdateService != null) {
aircraftUpdateService.stop();
}
}
}
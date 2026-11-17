package backend.simulation;

import backend.api.*;
import backend.models.*;
import java.io.IOException;
import java.util.*;

public class TrafficGenerationService {

private final DataLoader loader;
private final SimulationState simulation;
private final FlightGenerator generator;
private final SimulatedAircraftFactory factory;

private FlightSimulation flightSimulation;
private GeneratedTraffic generatedTraffic;
private List<Airport> airports;
private List<Waypoint> waypoints;
private List<WeatherZone> weatherZones;

public TrafficGenerationService(SimulationState simulation) {

this.loader = new DataLoader();
this.simulation = simulation;
this.generator = new FlightGenerator();
this.factory = new SimulatedAircraftFactory();
}

public GeneratedTraffic initialize(int flightCount) throws IOException {

List<Aircraft> aircraftCatalog = loader.loadAircraft();

airports = loader.loadAirports();
waypoints = loader.loadWaypoints();

generatedTraffic = generator.generateFlights(
flightCount,
aircraftCatalog,
airports,
waypoints
);

List<String> simulatedIds = factory.createAircraft(
simulation,
generatedTraffic,
waypoints
);

flightSimulation = new FlightSimulation(simulation, simulatedIds);

System.out.println("Generated flights: " + generatedTraffic.flights().size());
System.out.println("Simulated aircraft: " + simulatedIds.size());

return generatedTraffic;
}

public List<WeatherZone> refreshWeather() throws IOException {

if (airports == null) {
airports = loader.loadAirports();
}

WeatherDataService weatherService = new WeatherDataService();

weatherZones = weatherService.fetchWeatherZones(airports);

if (weatherZones.isEmpty()) {
System.out.println("Live weather unavailable. Loading demonstration weather.");
weatherZones = loader.loadWeather();
}

return weatherZones;
}

public void updateSimulation() {

if (flightSimulation != null) {
flightSimulation.updateAll();
}
}

public GeneratedTraffic getGeneratedTraffic() {
return generatedTraffic;
}

public List<WeatherZone> getWeatherZones() {
return weatherZones == null ? Collections.emptyList() : weatherZones;
}

public FlightSimulation getFlightSimulation() {
return flightSimulation;
}
}
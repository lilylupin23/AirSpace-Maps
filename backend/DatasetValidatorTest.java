package backend;

import backend.api.*;
import backend.models.*;
import java.util.*;

public class DatasetValidatorTest {

public static void main(String[] args) throws Exception {

DataLoader loader = new DataLoader();
DataValidator validator = new DataValidator();

List<Aircraft> aircraft = loader.loadAircraft();
List<Airport> airports = loader.loadAirports();
List<Flight> flights = loader.loadFlights();
List<Waypoint> waypoints = loader.loadWaypoints();
List<WeatherZone> weather = loader.loadWeather();
List<RestrictedZone> restricted = loader.loadRestrictedZones();

List<String> errors = validator.validateAll(
aircraft,
airports,
flights,
waypoints,
weather,
restricted
);

System.out.println("=== DATASET VALIDATION TEST ===");
System.out.println("Aircraft: " + aircraft.size());
System.out.println("Airports: " + airports.size());
System.out.println("Flights: " + flights.size());
System.out.println("Waypoints: " + waypoints.size());
System.out.println("Weather zones: " + weather.size());
System.out.println("Restricted zones: " + restricted.size());

if (errors.isEmpty()) {
System.out.println("\nPASS: All datasets passed validation.");
} else {
System.out.println("\nValidation errors: " + errors.size());

for (String error : errors) {
System.out.println("ERROR: " + error);
}
}
}
}
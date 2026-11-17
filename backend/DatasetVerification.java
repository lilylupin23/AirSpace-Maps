package backend;

import backend.api.DataLoader;
import backend.models.*;

import java.util.*;

public class DatasetVerification {

public static void main(String[] args) {

DataLoader loader = new DataLoader();

try {

List<Airport> airports = loader.loadAirports();
List<Aircraft> aircraft = loader.loadAircraft();
List<Flight> flights = loader.loadFlights();
List<Waypoint> waypoints = loader.loadWaypoints();
List<WeatherZone> weather = loader.loadWeather();
List<RestrictedZone> restricted = loader.loadRestrictedZones();

System.out.println("\n========== DATASET VERIFICATION ==========");

System.out.println("Airports: " + airports.size());
System.out.println("Aircraft: " + aircraft.size());
System.out.println("Flights: " + flights.size());
System.out.println("Waypoints: " + waypoints.size());
System.out.println("Weather zones: " + weather.size());
System.out.println("Restricted zones: " + restricted.size());

checkUnique("Airport IDs", airports.stream().map(Airport::airportId).toList());
checkUnique("Aircraft IDs", aircraft.stream().map(Aircraft::aircraftId).toList());
checkUnique("Flight IDs", flights.stream().map(Flight::flightId).toList());
checkUnique("Waypoint IDs", waypoints.stream().map(Waypoint::waypointId).toList());
checkUnique("Weather IDs", weather.stream().map(WeatherZone::weatherZoneId).toList());
checkUnique("Restricted zone IDs", restricted.stream().map(RestrictedZone::zoneId).toList());

Set<String> airportCodes = new HashSet<>();
for (Airport a : airports) {
airportCodes.add(a.icaoCode());
}

Set<String> aircraftIds = new HashSet<>();
for (Aircraft a : aircraft) {
aircraftIds.add(a.aircraftId());
}

Set<String> waypointIds = new HashSet<>();
for (Waypoint w : waypoints) {
waypointIds.add(w.waypointId());
}

for (Airport a : airports) {
checkCoordinate(a.latitude(), a.longitude(), "Airport " + a.airportId());
}

for (Waypoint w : waypoints) {

checkCoordinate(w.latitude(), w.longitude(), "Waypoint " + w.waypointId());

for (String connected : w.connectedWaypointIds()) {
if (!waypointIds.contains(connected)) {
System.out.println("ERROR: Unknown waypoint connection " + connected);
}
}

}

for (Flight f : flights) {

if (!aircraftIds.contains(f.aircraftId())) {
System.out.println("ERROR: Unknown aircraft in " + f.flightId());
}

if (!airportCodes.contains(f.departureAirport())) {
System.out.println("ERROR: Invalid departure airport in " + f.flightId());
}

if (!airportCodes.contains(f.arrivalAirport())) {
System.out.println("ERROR: Invalid arrival airport in " + f.flightId());
}

for (String id : f.route()) {
if (!waypointIds.contains(id)) {
System.out.println("ERROR: Unknown route waypoint " + id);
}
}

}

System.out.println("\nDataset verification completed.");

} catch (Exception e) {

System.out.println("DATASET VERIFICATION FAILED");
e.printStackTrace();

}

}

private static void checkUnique(String name, List<String> ids) {

Set<String> unique = new HashSet<>(ids);

if (unique.size() == ids.size()) {
System.out.println("PASS: " + name);
} else {
System.out.println("ERROR: Duplicate " + name);
}

}

private static void checkCoordinate(double lat, double lon, String name) {

if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
System.out.println("ERROR: Invalid coordinates: " + name);
}

}

}
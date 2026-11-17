
package backend.api;

import backend.models.*;
import java.util.*;

public class DataValidator {

public List<String> validateAll(
List<Aircraft> aircraft,
List<Airport> airports,
List<Flight> flights,
List<Waypoint> waypoints,
List<WeatherZone> weather,
List<RestrictedZone> restricted) {

List<String> errors = new ArrayList<>();

Set<String> aircraftIds = new HashSet<>();
Set<String> airportCodes = new HashSet<>();
Set<String> waypointIds = new HashSet<>();

for (Aircraft a : aircraft) {
if (!aircraftIds.add(a.aircraftId())) {
errors.add("Duplicate aircraft ID: " + a.aircraftId());
}

if (a.cruiseSpeed() <= 0 || a.serviceCeiling() <= 0 || a.capacity() < 0) {
errors.add("Invalid aircraft specifications: " + a.aircraftId());
}
}

for (Airport a : airports) {
if (!airportCodes.add(a.icaoCode())) {
errors.add("Duplicate airport ICAO code: " + a.icaoCode());
}

if (!validCoordinates(a.latitude(), a.longitude())) {
errors.add("Invalid airport coordinates: " + a.icaoCode());
}
}

for (Waypoint w : waypoints) {
if (!waypointIds.add(w.waypointId())) {
errors.add("Duplicate waypoint ID: " + w.waypointId());
}

if (!validCoordinates(w.latitude(), w.longitude())) {
errors.add("Invalid waypoint coordinates: " + w.waypointId());
}

if (w.minAltitude() > w.maxAltitude()) {
errors.add("Invalid waypoint altitude range: " + w.waypointId());
}
}

for (Waypoint w : waypoints) {
for (String connected : w.connectedWaypointIds()) {
if (!waypointIds.contains(connected)) {
errors.add("Unknown connected waypoint " + connected + " from " + w.waypointId());
}
}
}

for (Flight f : flights) {
if (!aircraftIds.contains(f.aircraftId())) {
errors.add("Flight references unknown aircraft: " + f.flightId());
}

if (!airportCodes.contains(f.departureAirport())) {
errors.add("Unknown departure airport: " + f.flightId());
}

if (!airportCodes.contains(f.arrivalAirport())) {
errors.add("Unknown arrival airport: " + f.flightId());
}

for (String waypoint : f.route()) {
if (!waypointIds.contains(waypoint)) {
errors.add("Flight references unknown waypoint: " + waypoint);
}
}

if (f.departureTime() == null) {
errors.add("Missing departure time: " + f.flightId());
}

if (f.cruiseAltitude() <= 0) {
errors.add("Invalid cruise altitude: " + f.flightId());
}
}

for (WeatherZone z : weather) {
if (!validCoordinates(z.latitude(), z.longitude())) {
errors.add("Invalid weather coordinates: " + z.weatherZoneId());
}

if (z.radius() <= 0 || z.minAltitude() > z.maxAltitude()) {
errors.add("Invalid weather zone: " + z.weatherZoneId());
}
}

for (RestrictedZone z : restricted) {
if (!validCoordinates(z.latitude(), z.longitude())) {
errors.add("Invalid restricted zone coordinates: " + z.zoneId());
}

if (z.radius() <= 0 || z.minAltitude() > z.maxAltitude()) {
errors.add("Invalid restricted zone: " + z.zoneId());
}
}

return errors;
}

private boolean validCoordinates(double latitude, double longitude) {
return latitude >= -90 && latitude <= 90
&& longitude >= -180 && longitude <= 180;
}
}
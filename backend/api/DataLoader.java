
package backend.api;

import backend.models.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.*;
import java.util.*;
import java.io.IOException;

public class DataLoader {
private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

private <T> List<T> loadJSON(String filename, TypeReference<List<T>> type) throws IOException {
Path path = Path.of("data", filename);

if (!Files.exists(path)) {
throw new IOException("Dataset not found: " + path);
}

return mapper.readValue(path.toFile(), type);
}

public List<Aircraft> loadAircraft() throws IOException {
return loadJSON("aircraft.json", new TypeReference<List<Aircraft>>() {});
}

public List<Flight> loadFlights() throws IOException {
return loadJSON("flights.json", new TypeReference<List<Flight>>() {});
}

public List<Waypoint> loadWaypoints() throws IOException {
return loadJSON("waypoints.json", new TypeReference<List<Waypoint>>() {});
}

public List<WeatherZone> loadWeather() throws IOException {
return loadJSON("weather.json", new TypeReference<List<WeatherZone>>() {});
}

public List<RestrictedZone> loadRestrictedZones() throws IOException {
return loadJSON("restricted_zones.json", new TypeReference<List<RestrictedZone>>() {});
}

public List<Airport> loadAirports() throws IOException {
Path path = Path.of("data", "airports.csv");

if (!Files.exists(path)) {
throw new IOException("Dataset not found: " + path);
}

List<String> lines = Files.readAllLines(path);
List<Airport> airports = new ArrayList<>();

for (int i = 1; i < lines.size(); i++) {
if (lines.get(i).isBlank()) continue;

String[] fields = lines.get(i).split(",", -1);

if (fields.length != 7) {
throw new IOException("Invalid airport record at line " + (i + 1));
}

airports.add(new Airport(
fields[0].trim(),
fields[1].trim(),
fields[2].trim(),
fields[3].trim(),
Double.parseDouble(fields[4].trim()),
Double.parseDouble(fields[5].trim()),
Double.parseDouble(fields[6].trim())
));
}

return airports;
}
}
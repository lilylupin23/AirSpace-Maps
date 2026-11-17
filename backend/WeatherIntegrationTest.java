package backend;

import backend.api.*;
import backend.models.*;
import java.util.*;

public class WeatherIntegrationTest {

public static void main(String[] args) throws Exception {

DataLoader loader = new DataLoader();

List<Airport> airports = loader.loadAirports();

WeatherDataService service = new WeatherDataService();

List<WeatherZone> zones = service.fetchWeatherZones(airports);

System.out.println("=== WEATHER INTEGRATION TEST ===");
System.out.println("Airports tested: " + airports.size());
System.out.println("Weather zones received: " + zones.size());

for (WeatherZone zone : zones) {
System.out.println(
zone.weatherZoneId()
+ " | " + zone.weatherType()
+ " | " + zone.severity()
);
}

if (!zones.isEmpty()) {
System.out.println("PASS: Weather data retrieved.");
} else {
System.out.println("FAIL: No weather data received.");
}
}
}
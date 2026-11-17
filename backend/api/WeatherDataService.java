package backend.api;

import backend.models.*;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.*;

public class WeatherDataService {

private final OpenMeteoAPI api;

public WeatherDataService() {
api = new OpenMeteoAPI();
}

public List<WeatherZone> fetchWeatherZones(List<Airport> airports) {

List<WeatherZone> zones = new ArrayList<>();

for (Airport airport : airports) {

try {

JsonNode response = api.fetchCurrentWeather(
airport.latitude(),
airport.longitude()
);

JsonNode current = response.get("current");

if (current == null) continue;

double precipitation = current.path("precipitation").asDouble(0);
double windSpeed = current.path("wind_speed_10m").asDouble(0);
int weatherCode = current.path("weather_code").asInt(-1);

String weatherType = classifyWeather(weatherCode, precipitation);
String severity = classifySeverity(weatherCode, precipitation, windSpeed);

zones.add(new WeatherZone(
"LIVE-WZ-" + airport.icaoCode(),
airport.latitude(),
airport.longitude(),
50,
severity,
weatherType,
0,
45000,
true
));

} catch (Exception e) {
System.out.println(
"Weather unavailable near " + airport.name() + ": " + e.getMessage()
);
}
}

return zones;
}

private String classifyWeather(int code, double precipitation) {

if (code >= 95) return "THUNDERSTORM";
if (code == 45 || code == 48) return "FOG";
if (code >= 71 && code <= 77) return "SNOW";
if (code >= 85 && code <= 86) return "SNOW";
if (code >= 51 && code <= 67) return "RAIN";
if (code >= 80 && code <= 82) return "RAIN";
if (precipitation > 0) return "RAIN";
if (code >= 1 && code <= 3) return "CLOUD";
if (code == 0) return "CLEAR";

return "UNKNOWN";
}

private String classifySeverity(
int code,
double precipitation,
double windSpeed) {

if (code >= 95 || precipitation >= 7 || windSpeed >= 50) {
return "HIGH";
}

if (code >= 45 || precipitation >= 2 || windSpeed >= 30) {
return "MODERATE";
}

return "LOW";
}
}
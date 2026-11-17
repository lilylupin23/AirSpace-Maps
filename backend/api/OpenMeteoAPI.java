package backend.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;

public class OpenMeteoAPI {

private final HttpClient client;
private final ObjectMapper mapper;

public OpenMeteoAPI() {
client = HttpClient.newBuilder()
.connectTimeout(Duration.ofSeconds(10))
.build();

mapper = new ObjectMapper();
}

public JsonNode fetchCurrentWeather(double latitude, double longitude) throws Exception {

String url = "https://api.open-meteo.com/v1/forecast"
+ "?latitude=" + latitude
+ "&longitude=" + longitude
+ "&current=temperature_2m,precipitation,weather_code,wind_speed_10m,wind_direction_10m"
+ "&timezone=UTC";

HttpRequest request = HttpRequest.newBuilder()
.uri(URI.create(url))
.timeout(Duration.ofSeconds(20))
.GET()
.build();

HttpResponse<String> response = client.send(
request,
HttpResponse.BodyHandlers.ofString()
);

if (response.statusCode() != 200) {
throw new RuntimeException("Weather API returned HTTP " + response.statusCode());
}

return mapper.readTree(response.body());
}
}
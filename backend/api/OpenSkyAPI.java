
package backend.api;

import java.net.URI;
import java.net.http.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OpenSkyAPI {
private static final String TOKEN_URL = "https://auth.opensky-network.org/auth/realms/opensky-network/protocol/openid-connect/token";
private static final String API_URL = "https://opensky-network.org/api/states/all";

private final HttpClient client = HttpClient.newHttpClient();
private final ObjectMapper mapper = new ObjectMapper();

private String accessToken;
private Instant tokenExpiry = Instant.MIN;

public synchronized void authenticate() throws Exception {
String id = System.getenv("OPENSKY_CLIENT_ID");
String secret = System.getenv("OPENSKY_CLIENT_SECRET");

if (id == null || secret == null) {
throw new IllegalStateException("OpenSky credentials missing");
}

String body = "grant_type=client_credentials"
+ "&client_id=" + URLEncoder.encode(id, StandardCharsets.UTF_8)
+ "&client_secret=" + URLEncoder.encode(secret, StandardCharsets.UTF_8);

HttpRequest request = HttpRequest.newBuilder()
.uri(URI.create(TOKEN_URL))
.header("Content-Type", "application/x-www-form-urlencoded")
.POST(HttpRequest.BodyPublishers.ofString(body))
.build();

HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

if (response.statusCode() != 200) {
throw new RuntimeException("Authentication failed: " + response.body());
}

JsonNode json = mapper.readTree(response.body());
accessToken = json.get("access_token").asText();
tokenExpiry = Instant.now().plusSeconds(json.get("expires_in").asLong());
}

public JsonNode fetchAircraft() throws Exception {
if (accessToken == null || Instant.now().isAfter(tokenExpiry.minusSeconds(60))) {
authenticate();
}

String url = API_URL + "?lamin=6.5&lamax=37.5&lomin=68.0&lomax=97.5";

HttpRequest request = HttpRequest.newBuilder()
.uri(URI.create(url))
.header("Authorization", "Bearer " + accessToken)
.GET()
.build();

HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

if (response.statusCode() == 401) {
accessToken = null;
return fetchAircraft();
}

if (response.statusCode() != 200) {
throw new RuntimeException("OpenSky error: HTTP " + response.statusCode());
}

return mapper.readTree(response.body());
}
}
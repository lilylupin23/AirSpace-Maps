package backend;

import backend.api.OpenSkyAPI;
import com.fasterxml.jackson.databind.JsonNode;

public class OpenSkyConnectionTest {
public static void main(String[] args) {
String id = System.getenv("OPENSKY_CLIENT_ID");
String secret = System.getenv("OPENSKY_CLIENT_SECRET");

if (id == null || secret == null) {
System.out.println("FAIL: OpenSky credentials not detected.");
return;
}

System.out.println("Credentials detected. Testing authentication...");

try {
OpenSkyAPI api = new OpenSkyAPI();
JsonNode data = api.fetchAircraft();

System.out.println("SUCCESS: OpenSky API connection established!");
System.out.println("Aircraft detected: " + data.path("states").size());
System.out.println("Response time: " + data.path("time").asLong());

} catch (Exception e) {
System.out.println("FAILED: " + e.getMessage());
}
}
}
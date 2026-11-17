package backend;

import backend.api.DataProcessor;
import backend.models.AircraftState;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class OpenSkyDataProcessorTest {

public static void main(String[] args) throws Exception {

ObjectMapper mapper = new ObjectMapper();
DataProcessor processor = new DataProcessor();

String json = """
{
"states": [
[
"abc123",
"TEST123",
"India",
1700000000,
1700000000,
77.1,
28.5,
10000.0,
false,
200.0,
90.0,
null,
null,
10000.0
]
]
}
""";

JsonNode response = mapper.readTree(json);

List<AircraftState> aircraft =
processor.processOpenSkyResponse(response);

System.out.println("=== OPENSKY DATA PROCESSOR TEST ===");
System.out.println("Aircraft processed: " + aircraft.size());

for (AircraftState state : aircraft) {
System.out.println("Aircraft ID: " + state.getAircraftId());
System.out.println("Position: " + state.getLatitude() + ", " + state.getLongitude());
System.out.println("Altitude: " + state.getAltitude());
System.out.println("Velocity: " + state.getVelocity());
}

if (!aircraft.isEmpty()) {
System.out.println("PASS: Aircraft data processed.");
} else {
System.out.println("FAIL: No aircraft processed.");
}
}
}
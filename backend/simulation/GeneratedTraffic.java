package backend.simulation;

import backend.models.Aircraft;
import backend.models.Flight;
import java.util.List;

public record GeneratedTraffic(
List<Aircraft> aircraft,
List<Flight> flights
) {
public GeneratedTraffic {
aircraft = List.copyOf(aircraft);
flights = List.copyOf(flights);
}
}
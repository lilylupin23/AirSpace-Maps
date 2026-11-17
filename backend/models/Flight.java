package backend.models;

import java.time.Instant;
import java.util.List;

public record Flight(
String flightId,
String callsign,
String aircraftId,
String departureAirport,
String arrivalAirport,
List<String> route,
Instant departureTime,
int cruiseAltitude,
String status,
String priority
) {}
package backend.models;

public record Airport(
String airportId,
String name,
String icaoCode,
String state,
double latitude,
double longitude,
double elevation
) {}
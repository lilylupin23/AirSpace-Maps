package backend.models;

public record WeatherZone(
String weatherZoneId,
double latitude,
double longitude,
double radius,
String severity,
String weatherType,
int minAltitude,
int maxAltitude,
boolean active
) {}
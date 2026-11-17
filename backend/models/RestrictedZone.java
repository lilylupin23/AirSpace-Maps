package backend.models;

public record RestrictedZone(
String zoneId,
String zoneName,
double latitude,
double longitude,
double radius,
int minAltitude,
int maxAltitude,
String restrictionType,
boolean active
) {}
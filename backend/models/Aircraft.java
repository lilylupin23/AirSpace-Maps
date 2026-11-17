package backend.models;

public record Aircraft(
String aircraftId,
String registration,
String model,
String category,
double cruiseSpeed,
int serviceCeiling,
int capacity
) {}
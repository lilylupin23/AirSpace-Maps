
package backend.simulation;

public record CollisionAlert(
String aircraftOne,
String aircraftTwo,
int secondsToConflict,
double horizontalDistanceKm,
double verticalSeparationMeters
) {}
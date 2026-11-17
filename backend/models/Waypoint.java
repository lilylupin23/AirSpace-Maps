package backend.models;

import java.util.List;

public record Waypoint(
String waypointId,
String name,
double latitude,
double longitude,
int minAltitude,
int maxAltitude,
List<String> connectedWaypointIds
) {}
package backend.models;

import java.time.Instant;

public record RoutePoint(
Waypoint waypoint,
int altitude,
Instant estimatedArrivalTime
) {}
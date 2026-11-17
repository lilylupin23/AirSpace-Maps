package backend;

import backend.api.DataLoader;
import backend.models.*;
import backend.simulation.RoutePlanner;
import java.time.Instant;
import java.util.*;

public class RoutePlannerTest {

public static void main(String[] args) throws Exception {

DataLoader loader = new DataLoader();

List<Waypoint> waypoints = loader.loadWaypoints();

RoutePlanner planner = new RoutePlanner();

List<RoutePoint> route = planner.findRoute(
"TEST-AIRCRAFT",
"WP001",
"WP003",
30000,
30000,
Instant.now(),
230,
waypoints,
Collections.emptyList(),
Collections.emptyList(),
Collections.emptyList()
);

System.out.println("=== 4D A* ROUTE PLANNING TEST ===");
System.out.println("Start: WP001");
System.out.println("Destination: WP003");
System.out.println("Route points: " + route.size());

for (RoutePoint point : route) {
System.out.println(
point.waypoint().waypointId()
+ " | Altitude: " + point.altitude()
+ " ft | ETA: " + point.estimatedArrivalTime()
);
}

if (!route.isEmpty()) {
System.out.println("PASS: Route successfully generated.");
} else {
System.out.println("FAIL: No route found.");
}
}
}
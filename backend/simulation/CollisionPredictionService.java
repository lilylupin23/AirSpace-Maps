
package backend.simulation;

import backend.models.AircraftState;
import java.util.*;

public class CollisionPredictionService {
private static final double EARTH_RADIUS = 6371000;
private static final double HORIZONTAL_LIMIT = 9260;
private static final double VERTICAL_LIMIT = 304.8;
private static final int PREDICTION_TIME = 600;
private static final int TIME_STEP = 30;

public List<CollisionAlert> detectCollisions(SimulationState state) {
List<AircraftState> aircraft = new ArrayList<>(state.getAircraftStates());
List<CollisionAlert> alerts = new ArrayList<>();

for (int i = 0; i < aircraft.size(); i++) {
for (int j = i + 1; j < aircraft.size(); j++) {
AircraftState a = aircraft.get(i);
AircraftState b = aircraft.get(j);

for (int t = 0; t <= PREDICTION_TIME; t += TIME_STEP) {
double[] posA = predict(a, t);
double[] posB = predict(b, t);

double horizontal = distance(posA[0], posA[1], posB[0], posB[1]);
double vertical = Math.abs(a.getAltitude() - b.getAltitude());

if (horizontal <= HORIZONTAL_LIMIT && vertical <= VERTICAL_LIMIT) {
alerts.add(new CollisionAlert(
a.getAircraftId(),
b.getAircraftId(),
t,
horizontal / 1000.0,
vertical
));
break;
}
}
}
}

return alerts;
}

private double[] predict(AircraftState a, int seconds) {
double distance = a.getVelocity() * seconds;
double bearing = Math.toRadians(a.getHeading());
double lat1 = Math.toRadians(a.getLatitude());
double lon1 = Math.toRadians(a.getLongitude());

double lat2 = Math.asin(
Math.sin(lat1) * Math.cos(distance / EARTH_RADIUS)
+ Math.cos(lat1) * Math.sin(distance / EARTH_RADIUS) * Math.cos(bearing)
);

double lon2 = lon1 + Math.atan2(
Math.sin(bearing) * Math.sin(distance / EARTH_RADIUS) * Math.cos(lat1),
Math.cos(distance / EARTH_RADIUS) - Math.sin(lat1) * Math.sin(lat2)
);

return new double[]{Math.toDegrees(lat2), Math.toDegrees(lon2)};
}

private double distance(double lat1, double lon1, double lat2, double lon2) {
double dLat = Math.toRadians(lat2 - lat1);
double dLon = Math.toRadians(lon2 - lon1);

double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
* Math.sin(dLon / 2) * Math.sin(dLon / 2);

return 2 * EARTH_RADIUS * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
}
}
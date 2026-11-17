package backend.models;

import java.time.Instant;

public class AircraftState {

private final String aircraftId;
private double latitude;
private double longitude;
private double altitude;
private double velocity;
private double heading;
private String status;
private Instant lastUpdated;

public AircraftState(
String aircraftId,
double latitude,
double longitude,
double altitude,
double velocity,
double heading) {

this(aircraftId, latitude, longitude, altitude, velocity, heading, Instant.now());
}

public AircraftState(
String aircraftId,
double latitude,
double longitude,
double altitude,
double velocity,
double heading,
Instant lastUpdated) {

this.aircraftId = aircraftId;
this.latitude = latitude;
this.longitude = longitude;
this.altitude = altitude;
this.velocity = velocity;
this.heading = heading;
this.status = "AIRBORNE";
this.lastUpdated = lastUpdated;
}

public synchronized void updatePosition(
double latitude,
double longitude,
double altitude,
double velocity,
double heading) {

updatePosition(latitude, longitude, altitude, velocity, heading, Instant.now());
}

public synchronized void updatePosition(
double latitude,
double longitude,
double altitude,
double velocity,
double heading,
Instant timestamp) {

this.latitude = latitude;
this.longitude = longitude;
this.altitude = altitude;
this.velocity = velocity;
this.heading = heading;
this.lastUpdated = timestamp;
}

public String getAircraftId() { return aircraftId; }
public synchronized double getLatitude() { return latitude; }
public synchronized double getLongitude() { return longitude; }
public synchronized double getAltitude() { return altitude; }
public synchronized double getVelocity() { return velocity; }
public synchronized double getHeading() { return heading; }
public synchronized String getStatus() { return status; }
public synchronized void setStatus(String status) { this.status = status; }
public synchronized Instant getLastUpdated() { return lastUpdated; }
}
package backend.api;

import backend.models.AircraftState;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class DataProcessor {

private static final double MIN_ALTITUDE = -500;
private static final double MAX_ALTITUDE = 20000;
private static final double MAX_VELOCITY = 400;
private static final long MAX_AGE_SECONDS = 180;

public List<AircraftState> processOpenSkyResponse(JsonNode response) {

if (response == null || !response.has("states") || !response.get("states").isArray()) {
throw new IllegalArgumentException("Invalid OpenSky response");
}

Map<String, AircraftState> uniqueAircraft = new HashMap<>();
Instant now = Instant.now();

for (JsonNode state : response.get("states")) {

if (!state.isArray() || state.size() <= 13) continue;

String aircraftId = getString(state, 0);

if (aircraftId == null || aircraftId.isBlank()) continue;

Double longitude = getNumber(state, 5);
Double latitude = getNumber(state, 6);

if (latitude == null || longitude == null) continue;
if (latitude < -90 || latitude > 90) continue;
if (longitude < -180 || longitude > 180) continue;

Double barometricAltitude = getNumber(state, 7);
Double velocity = getNumber(state, 9);
Double heading = getNumber(state, 10);
Double geometricAltitude = getNumber(state, 13);
Double lastContact = getNumber(state, 4);

boolean onGround = state.size() > 8 && state.get(8).asBoolean(false);

if (lastContact == null) continue;

Instant timestamp = Instant.ofEpochSecond(lastContact.longValue());

long age = Duration.between(timestamp, now).getSeconds();

if (age < -30 || age > MAX_AGE_SECONDS) continue;

Double altitude = barometricAltitude != null
? barometricAltitude
: geometricAltitude;

if (altitude == null) {
if (onGround) altitude = 0.0;
else continue;
}

if (velocity == null) {
if (onGround) velocity = 0.0;
else continue;
}

if (heading == null) {
if (onGround) heading = 0.0;
else continue;
}

if (!isValidNumber(altitude) || !isValidNumber(velocity) || !isValidNumber(heading)) continue;

if (altitude < MIN_ALTITUDE || altitude > MAX_ALTITUDE) continue;
if (velocity < 0 || velocity > MAX_VELOCITY) continue;
if (heading < 0 || heading >= 360) continue;

AircraftState aircraft = new AircraftState(
aircraftId,
latitude,
longitude,
altitude,
velocity,
heading,
timestamp
);

AircraftState existing = uniqueAircraft.get(aircraftId);

if (existing == null || timestamp.isAfter(existing.getLastUpdated())) {
uniqueAircraft.put(aircraftId, aircraft);
}
}

return new ArrayList<>(uniqueAircraft.values());
}

private String getString(JsonNode state, int index) {

if (state.size() <= index) return null;

JsonNode value = state.get(index);

if (value == null || value.isNull()) return null;

if (!value.isTextual()) return null;

return value.asText().trim();
}

private Double getNumber(JsonNode state, int index) {

if (state.size() <= index) return null;

JsonNode value = state.get(index);

if (value == null || !value.isNumber()) return null;

double number = value.asDouble();

if (!Double.isFinite(number)) return null;

return number;
}

private boolean isValidNumber(double value) {
return Double.isFinite(value);
}

public List<AircraftState> filterActiveAircraft(
Collection<AircraftState> aircraft,
long maxAgeSeconds) {

List<AircraftState> processed = new ArrayList<>();
Instant now = Instant.now();

for (AircraftState a : aircraft) {

if (a == null || a.getLastUpdated() == null) continue;

if (!isValidNumber(a.getLatitude()) || !isValidNumber(a.getLongitude())) continue;
if (!isValidNumber(a.getAltitude()) || !isValidNumber(a.getVelocity())) continue;
if (!isValidNumber(a.getHeading())) continue;

if (a.getLatitude() < -90 || a.getLatitude() > 90) continue;
if (a.getLongitude() < -180 || a.getLongitude() > 180) continue;

if (a.getAltitude() < MIN_ALTITUDE || a.getAltitude() > MAX_ALTITUDE) continue;
if (a.getVelocity() < 0 || a.getVelocity() > MAX_VELOCITY) continue;
if (a.getHeading() < 0 || a.getHeading() >= 360) continue;

long age = Duration.between(a.getLastUpdated(), now).getSeconds();

if (age >= 0 && age <= maxAgeSeconds) {
processed.add(a);
}
}

return processed;
}
}
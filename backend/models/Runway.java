
package backend.models;

public class Runway {
private final String runwayId;
private final String airportId;
private final int lengthMeters;
private String assignedFlight;

public Runway(String runwayId, String airportId, int lengthMeters) {
this.runwayId = runwayId;
this.airportId = airportId;
this.lengthMeters = lengthMeters;
this.assignedFlight = null;
}

public synchronized boolean assignFlight(String flightId) {
if (assignedFlight != null) return false;
assignedFlight = flightId;
return true;
}

public synchronized void release() {
assignedFlight = null;
}

public String getRunwayId() { return runwayId; }
public String getAirportId() { return airportId; }
public int getLengthMeters() { return lengthMeters; }
public synchronized String getAssignedFlight() { return assignedFlight; }
public synchronized boolean isAvailable() { return assignedFlight == null; }
}
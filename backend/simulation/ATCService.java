package backend.simulation;

public class ATCService {

private final TrafficManager trafficManager;

public ATCService(TrafficManager trafficManager) {
this.trafficManager = trafficManager;
}

public String issueHold(String id) {

if (!trafficManager.holdAircraft(id)) {
return "Unable to issue HOLD. Aircraft may not be simulated.";
}

return "HOLD instruction issued to " + id;
}

public String issueResume(String id) {

if (!trafficManager.resumeAircraft(id)) {
return "Unable to issue RESUME. Aircraft may not be simulated.";
}

return "RESUME instruction issued to " + id;
}

public String getLastInstruction(String id) {
return trafficManager.getInstruction(id);
}
}
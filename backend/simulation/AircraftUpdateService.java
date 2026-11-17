
package backend.simulation;

import backend.api.AircraftDataService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AircraftUpdateService {
private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
private final AircraftDataService dataService;

public AircraftUpdateService(SimulationState state) {
dataService = new AircraftDataService(state);
}

public void start() {
scheduler.scheduleAtFixedRate(() -> {
try {
dataService.refreshAircraft();
} catch (Exception e) {
System.out.println("Aircraft update failed: " + e.getMessage());
}
}, 0, 2, TimeUnit.MINUTES);
}

public void stop() {
scheduler.shutdownNow();
}
}

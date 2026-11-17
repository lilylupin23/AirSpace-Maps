package backend;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

private BackendController controller;

@Override
public void start(Stage stage) {

stage.setTitle("AirSpaceMaps");
stage.setWidth(1200);
stage.setHeight(800);
stage.show();

controller = new BackendController();

Thread backendThread = new Thread(() -> {
try {
controller.initialize();
} catch (Exception e) {
System.out.println("Backend initialization failed.");
e.printStackTrace();
}
});

backendThread.setDaemon(true);
backendThread.start();
}

@Override
public void stop() {
if (controller != null) {
controller.shutdown();
}

System.out.println("AirSpaceMaps stopped.");
}

public BackendController getController() {
return controller;
}

public static void main(String[] args) {
launch(args);
}
}
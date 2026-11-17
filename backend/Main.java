
package backend;

import backend.frontend.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

private BackendController controller;

@Override
public void start(Stage stage) {

controller = new BackendController();

MainView mainView = new MainView();

Scene scene = new Scene(mainView, 1400, 850);

stage.setTitle("AirSpaceMaps | Aviation Control System");
stage.setMinWidth(1100);
stage.setMinHeight(700);
stage.setScene(scene);
stage.setOnCloseRequest(event -> {
if (controller != null) {
controller.shutdown();
}
});
stage.show();

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

public static void main(String[] args) {
launch(args);
}
}
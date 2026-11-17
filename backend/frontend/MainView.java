
package backend.frontend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainView extends BorderPane {

private VBox sidebar;
private VBox content;
private Label pageTitle;
private Label pageDescription;
private Button activeButton;

private final String[] pages = {
"Dashboard",
"Live Airspace",
"Flight Management",
"Route Planner",
"Collision Monitoring",
"Weather Monitoring",
"ATC Operations"
};

public MainView() {
getStyleClass().add("app-root");
createSidebar();
createMainArea();

try {
getStylesheets().add(
java.nio.file.Path.of("frontend", "styles.css").toUri().toString()
);
} catch (Exception e) {
System.out.println("Could not load stylesheet: " + e.getMessage());
}

showPage("Dashboard");
}

private void createSidebar() {
sidebar = new VBox(12);
sidebar.getStyleClass().add("sidebar");
sidebar.setPadding(new Insets(25, 15, 25, 15));
sidebar.setPrefWidth(230);

Label logo = new Label("AIRSPACE");
logo.getStyleClass().add("logo");

Label subtitle = new Label("AVIATION CONTROL");
subtitle.getStyleClass().add("sidebar-subtitle");

VBox branding = new VBox(5, logo, subtitle);
branding.setPadding(new Insets(5, 10, 30, 10));

sidebar.getChildren().add(branding);

for (String page : pages) {
Button button = new Button(page);
button.getStyleClass().add("nav-button");
button.setMaxWidth(Double.MAX_VALUE);
button.setAlignment(Pos.CENTER_LEFT);
button.setOnAction(event -> showPage(page));
sidebar.getChildren().add(button);
}
}

private void createMainArea() {
VBox mainArea = new VBox(0);
mainArea.getStyleClass().add("main-area");

HBox header = new HBox();
header.getStyleClass().add("topbar");
header.setAlignment(Pos.CENTER_LEFT);
header.setPadding(new Insets(15, 25, 15, 25));

Label systemName = new Label("AIRSPACE MONITORING SYSTEM");
systemName.getStyleClass().add("topbar-title");

Label systemStatus = new Label("● SYSTEM ONLINE");
systemStatus.getStyleClass().add("system-status");

HBox headerRight = new HBox(systemStatus);
headerRight.setAlignment(Pos.CENTER_RIGHT);

HBox.setHgrow(systemName, javafx.scene.layout.Priority.ALWAYS);
header.getChildren().addAll(systemName, headerRight);

content = new VBox(12);
content.getStyleClass().add("page-content");
content.setPadding(new Insets(45, 50, 45, 50));
pageTitle = new Label();
pageTitle.getStyleClass().add("page-title");

pageDescription = new Label();
pageDescription.getStyleClass().add("page-description");

VBox pageHeader = new VBox(7, pageTitle, pageDescription);
content.getChildren().add(pageHeader);

mainArea.getChildren().addAll(header, content);

setLeft(sidebar);
setCenter(mainArea);
}

private void showPage(String page) {
pageTitle.setText(page.toUpperCase());
pageDescription.setText(getDescription(page));

content.getChildren().removeIf(node -> node != content.getChildren().get(0));

Label placeholder = new Label(
page.equals("Dashboard")
? "Welcome to AirSpaceMaps. Your aviation overview will appear here."
: page + " interface will appear here."
);

placeholder.getStyleClass().add("placeholder");
placeholder.setWrapText(true);

content.getChildren().add(placeholder);

for (javafx.scene.Node node : sidebar.getChildren()) {
if (node instanceof Button button) {
boolean selected = button.getText().equals(page);
button.getStyleClass().remove("active");
if (selected) {
button.getStyleClass().add("active");
}
}
}
}

private String getDescription(String page) {
return switch (page) {
case "Dashboard" -> "Pan-India aviation overview and system status";
case "Live Airspace" -> "Monitor live and simulated aircraft across India";
case "Flight Management" -> "View and manage active flight records";
case "Route Planner" -> "Plan routes and avoid restricted airspace";
case "Collision Monitoring" -> "Monitor predicted aircraft conflicts";
case "Weather Monitoring" -> "View weather conditions across monitored regions";
case "ATC Operations" -> "Issue and monitor air traffic control instructions";
default -> "";
};
}
}
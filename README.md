# AirSpaceMaps ✈️
### Pan-India 4D Air Traffic Navigation and Monitoring System

AirSpaceMaps is a Java-based air traffic navigation and monitoring system designed to simulate, visualize, and analyze aircraft movement across Indian airspace.

The project combines real-time aircraft tracking, flight simulation, collision prediction, 4D route planning, weather integration, and air traffic control functionalities.

## Features

### 1. Aircraft Management and Tracking
- Real-time aircraft position retrieval using the OpenSky Network API.
- Aircraft data processing and validation.
- Dynamic generation of simulated air traffic.
- Aircraft position and movement tracking.

### 2. 4D Collision Prediction
- Predicts potential conflicts between aircraft.
- Evaluates horizontal and vertical separation.
- Estimates time to potential conflict.
- Generates collision alerts.

### 3. 4D Route Planning
- A* based route planning across connected waypoints.
- Considers altitude and time during route planning.
- Supports weather and restricted-zone avoidance.
- Evaluates potential conflicts with existing air traffic.

### 4. Weather Integration
- Retrieves current weather information through the Open-Meteo API.
- Processes weather conditions around selected airports.
- Supports weather-aware route planning.

### 5. Airport Operations and ATC
- Aircraft hold and resume commands.
- Simulated air traffic management.
- Centralized access to backend services through `BackendController`.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Core application development |
| Maven | Dependency and build management |
| JavaFX | Desktop application interface |
| Jackson | JSON processing |
| OpenSky Network API | Live aircraft tracking |
| Open-Meteo API | Weather data |

## Project Structure

```text
AirSpace-Maps/
│
├── backend/
│   ├── api/
│   ├── models/
│   ├── simulation/
│   ├── Main.java
│   └── BackendController.java
│
├── frontend/
│
├── data/
│   ├── airports.csv
│   ├── aircraft.json
│   ├── flights.json
│   ├── waypoints.json
│   ├── weather.json
│   └── restricted_zones.json
│
├── pom.xml
├── README.md
└── .gitignore
```

## Installation and Setup

### Prerequisites
- Java Development Kit (JDK) 21
- Apache Maven 3.9 or later
- Git

### Clone the Repository

```bash
git clone YOUR_REPOSITORY_URL
cd AirSpace-Maps
```

### Configure OpenSky API Credentials

Set the following environment variables:

```text
OPENSKY_CLIENT_ID
OPENSKY_CLIENT_SECRET
```

Obtain your credentials through the OpenSky Network account system. Never commit API credentials to the repository.

### Build the Project

```bash
mvn clean package
```

### Run the Application

```bash
mvn javafx:run
```

## Data Sources

- **OpenSky Network:** Real-time ADS-B-derived aircraft position data.
- **Open-Meteo:** Current weather information.
- **Local datasets:** Aircraft specifications, airports, flight records, waypoints, and demonstration restricted zones.

## Project Limitations

- Live aircraft coverage depends on ADS-B reception and API availability.
- Weather information is indicative and is not a substitute for aviation-grade METAR, TAF, or SIGMET data.
- Some flight records, weather zones, and restricted zones are simulated for demonstration purposes.
- Collision prediction and route planning use simplified educational models.

**AirSpaceMaps is an academic simulation project and is not intended for operational air traffic control or real-world flight navigation.**

## Project Status

**Backend:** Core modules implemented with initial integration testing.

**Frontend:** Under development.

---

*Developed as an academic project exploring aviation systems, algorithms, real-time data processing, and software engineering.*

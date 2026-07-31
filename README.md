# scada-simulator

A SCADA (Supervisory Control and Data Acquisition) simulator for oil & gas field
equipment. It generates synthetic sensor readings (well pressure, temperature,
flow rate, pump vibration, pump RPM), streams them over MQTT, persists them to
PostgreSQL, and raises threshold-based alerts — a small end-to-end pipeline
that mimics how real industrial telemetry systems are wired together.

## Architecture

```
SensorSimulator  --publish-->  MQTT Broker  --subscribe-->  MqttSubscriberService
(scheduled task)               (sensors/#)                        |
                                                                    v
                                                      SensorReadingRepository (Postgres)
                                                                    |
                                                                    v
                                                              AlertService
                                                          (threshold checks)
                                                                    |
                                                                    v
                                                        AlertRepository (Postgres)

REST clients  <---  SensorController (/api/sensors/*)
REST clients  <---  AlertController  (/api/alerts/*)
```

- **`SensorSimulator`** publishes 5 randomized readings every 5 seconds to
  MQTT topics under `sensors/<sensorId>`, with a 5% chance per reading of an
  out-of-range "anomaly" spike.
- **`MqttSubscriberService`** subscribes to `sensors/#`, deserializes each
  message, saves it as a `SensorReading`, and forwards it to `AlertService`.
- **`AlertService`** compares each reading against per-sensor-type WARNING and
  CRITICAL thresholds and writes an `Alert` row when a threshold is exceeded.
- **`SensorController`** / **`AlertController`** expose the stored data over a
  REST API.

Everything runs in a single Spring Boot process (`scada-backend`); the
simulator and the subscriber are just two components inside it, connected to
each other only through the MQTT broker.

## Simulated sensors

| Sensor ID       | Type          | Normal range   | Unit     |
|-----------------|---------------|----------------|----------|
| `WELL-001-PRES` | pressure      | 800 – 1200     | PSI      |
| `WELL-001-TEMP` | temperature   | 150 – 250      | °F       |
| `WELL-001-FLOW` | flow_rate     | 500 – 1500     | BBL/day  |
| `PUMP-001-VIB`  | vibration     | 0.5 – 5.0      | mm/s     |
| `PUMP-001-RPM`  | rpm           | 1500 – 3600    | RPM      |

## Alert thresholds

| Sensor type | Warning | Critical |
|-------------|---------|----------|
| pressure    | > 1200  | > 1400   |
| temperature | > 250   | > 300    |
| flow_rate   | > 1500  | > 1800   |
| vibration   | > 5.0   | > 7.0    |
| rpm         | > 3600  | > 4000   |

## Prerequisites

- Java 21
- PostgreSQL, with a database named `scada`
- An MQTT broker listening on `tcp://localhost:1883` (e.g. [Mosquitto](https://mosquitto.org/))
- Maven (or use the included wrapper, `./mvnw`)

## Configuration

Connection settings live in
`scada-backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/scada
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:}
```

`spring.jpa.hibernate.ddl-auto=update` is set, so the `sensor_readings` and
`alerts` tables are created/updated automatically on startup — no manual
migrations are needed. The datasource username/password default to
`postgres` / empty and can be overridden with the `DB_USERNAME` and
`DB_PASSWORD` environment variables for your local Postgres setup:

```bash
export DB_USERNAME=myuser
export DB_PASSWORD=mypassword
```

The MQTT broker address (`tcp://localhost:1883`) is currently hardcoded in
`SensorSimulator` and `MqttSubscriberService`.

## Running

```bash
# start a local MQTT broker, e.g.:
mosquitto

# in scada-backend/
./mvnw spring-boot:run
```

On startup the app will:
1. Connect to the MQTT broker and start publishing simulated readings every 5 seconds.
2. Connect to the MQTT broker again and subscribe to `sensors/#`.
3. Persist every reading it receives and evaluate it for alerts.

## REST API

### Sensor readings

| Method | Path                                  | Description                                   |
|--------|----------------------------------------|------------------------------------------------|
| GET    | `/api/sensors/readings`                | Readings from the last hour, newest first       |
| GET    | `/api/sensors/readings/{sensorId}`     | All readings for a given sensor, newest first   |
| GET    | `/api/sensors/readings/type/{sensorType}` | All readings for a given sensor type, newest first |

### Alerts

| Method | Path                          | Description                                  |
|--------|-------------------------------|-----------------------------------------------|
| GET    | `/api/alerts`                 | Unacknowledged alerts, newest first            |
| GET    | `/api/alerts/all`              | All alerts, newest first                       |
| PUT    | `/api/alerts/{id}/acknowledge`| Marks an alert as acknowledged                 |

## Project structure

```
scada-backend/
  src/main/java/com/energyprojects/scada/
    ScadaBackendApplication.java   # Spring Boot entry point
    simulator/SensorSimulator.java     # generates & publishes fake readings via MQTT
    service/MqttSubscriberService.java # subscribes to MQTT, persists readings
    service/AlertService.java          # threshold checks -> alerts
    controller/SensorController.java   # REST API for readings
    controller/AlertController.java    # REST API for alerts
    model/SensorReading.java           # JPA entity
    model/Alert.java                   # JPA entity
    repository/                        # Spring Data JPA repositories
```

## Known limitations

- The MQTT broker host/port is hardcoded rather than externally configured.
- There's no authentication on the REST API.
- No WebSocket/live-streaming endpoint yet, despite the WebSocket/STOMP
  dependencies being present in `pom.xml` — real-time delivery to clients is
  not yet wired up.

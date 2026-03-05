package com.energyprojects.scada.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sensorId;
    private String sensorType;
    private Double value;
    private Double threshold;
    private String severity;
    private String message;
    private boolean acknowledged;
    private Instant timestamp;

    public Alert() {}

    public Alert(String sensorId, String sensorType, Double value,
                 Double threshold, String severity, String message, Instant timestamp) {
        this.sensorId = sensorId;
        this.sensorType = sensorType;
        this.value = value;
        this.threshold = threshold;
        this.severity = severity;
        this.message = message;
        this.acknowledged = false;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public String getSensorId() { return sensorId; }
    public String getSensorType() { return sensorType; }
    public Double getValue() { return value; }
    public Double getThreshold() { return threshold; }
    public String getSeverity() { return severity; }
    public String getMessage() { return message; }
    public boolean isAcknowledged() { return acknowledged; }
    public Instant getTimestamp() { return timestamp; }

    public void setId(Long id) { this.id = id; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    public void setSensorType(String sensorType) { this.sensorType = sensorType; }
    public void setValue(Double value) { this.value = value; }
    public void setThreshold(Double threshold) { this.threshold = threshold; }
    public void setSeverity(String severity) { this.severity = severity; }
    public void setMessage(String message) { this.message = message; }
    public void setAcknowledged(boolean acknowledged) { this.acknowledged = acknowledged; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
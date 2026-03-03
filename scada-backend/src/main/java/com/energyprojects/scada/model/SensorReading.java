package com.energyprojects.scada.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sensor_readings")
public class SensorReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sensorId;
    private String sensorType;
    private Double value;
    private String unit;
    private Instant timestamp;

    public SensorReading() {}

    public SensorReading(String sensorId, String sensorType, Double value, String unit, Instant timestamp) {
        this.sensorId = sensorId;
        this.sensorType = sensorType;
        this.value = value;
        this.unit = unit;
        this.timestamp = timestamp;
    }

    // Getters
    public Long getId() { return id; }
    public String getSensorId() { return sensorId; }
    public String getSensorType() { return sensorType; }
    public Double getValue() { return value; }
    public String getUnit() { return unit; }
    public Instant getTimestamp() { return timestamp; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    public void setSensorType(String sensorType) { this.sensorType = sensorType; }
    public void setValue(Double value) { this.value = value; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
package com.energyprojects.scada.service;

import com.energyprojects.scada.model.Alert;
import com.energyprojects.scada.model.SensorReading;
import com.energyprojects.scada.repository.AlertRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    private static final Map<String, Double> WARNING_THRESHOLDS = Map.of(
        "pressure", 1200.0,
        "temperature", 250.0,
        "flow_rate", 1500.0,
        "vibration", 5.0,
        "rpm", 3600.0
    );

    private static final Map<String, Double> CRITICAL_THRESHOLDS = Map.of(
        "pressure", 1400.0,
        "temperature", 300.0,
        "flow_rate", 1800.0,
        "vibration", 7.0,
        "rpm", 4000.0
    );

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public void checkReading(SensorReading reading) {
        Double criticalThreshold = CRITICAL_THRESHOLDS.get(reading.getSensorType());
        Double warningThreshold = WARNING_THRESHOLDS.get(reading.getSensorType());

        if (criticalThreshold != null && reading.getValue() > criticalThreshold) {
            createAlert(reading, criticalThreshold, "CRITICAL");
        } else if (warningThreshold != null && reading.getValue() > warningThreshold) {
            createAlert(reading, warningThreshold, "WARNING");
        }
    }

    private void createAlert(SensorReading reading, Double threshold, String severity) {
        String message = String.format("%s on %s: %.2f exceeds %s threshold of %.2f",
            reading.getSensorType(), reading.getSensorId(),
            reading.getValue(), severity.toLowerCase(), threshold);

        Alert alert = new Alert(
            reading.getSensorId(),
            reading.getSensorType(),
            reading.getValue(),
            threshold,
            severity,
            message,
            Instant.now()
        );

        alertRepository.save(alert);
        System.out.println("ALERT [" + severity + "]: " + message);
    }
}
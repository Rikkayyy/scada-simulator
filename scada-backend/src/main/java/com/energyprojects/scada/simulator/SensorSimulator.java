package com.energyprojects.scada.simulator;

import com.energyprojects.scada.model.SensorReading;
import com.energyprojects.scada.repository.SensorReadingRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Random;

@Component
public class SensorSimulator {
    
    private final SensorReadingRepository repository;
    private final Random random = new Random();

    public SensorSimulator(SensorReadingRepository repository) {
        this.repository = repository;
    }

    @Scheduled(fixedRate = 5000) // Every 5 seconds
    public void generateReadings() {
        generateReading("WELL-001-PRES", "pressure", 800, 1200, "PSI");
        generateReading("WELL-001-TEMP", "temperature", 150, 250, "°F");
        generateReading("WELL-001-FLOW", "flow_rate", 500, 1500, "BBL/day");
        generateReading("PUMP-001-VIB", "vibration", 0.5, 5.0, "mm/s");
        generateReading("PUMP-001-RPM", "rpm", 1500, 3600, "RPM");

        System.out.println("Generated 5 sesnor readings at " + Instant.now());
    }

    private void generateReading(String sensorId, String sensorType, double min, double max, String unit) {
        double value = min + (random.nextDouble() * (max - min));

        //  5% chance of an anomaly - value spikes outside normal range
        if (random.nextDouble() < 0.05) {
            value = max + (random.nextDouble() * max * 0.3);
        }

        value = Math.round(value * 100.0) / 100.0;

        SensorReading reading = new SensorReading(sensorId, sensorType, value, unit, Instant.now());

        repository.save(reading);
    }
}

package com.energyprojects.scada.simulator;

import java.time.Instant;
import java.util.Random;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.energyprojects.scada.model.SensorReading;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
public class SensorSimulator {
    
    // private final SensorReadingRepository repository;
    private final MqttClient mqttClient;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();

    public SensorSimulator() throws Exception {
        this.mqttClient = new MqttClient("tcp://localhost:1883", "sensor-simulator");
        MqttConnectionOptions options = new MqttConnectionOptions();
        this.mqttClient.connect(options);

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Scheduled(fixedRate = 5000) // Every 5 seconds
    public void generateReadings() {
        generateReading("WELL-001-PRES", "pressure", 800, 1200, "PSI");
        generateReading("WELL-001-TEMP", "temperature", 150, 250, "°F");
        generateReading("WELL-001-FLOW", "flow_rate", 500, 1500, "BBL/day");
        generateReading("PUMP-001-VIB", "vibration", 0.5, 5.0, "mm/s");
        generateReading("PUMP-001-RPM", "rpm", 1500, 3600, "RPM");

        System.out.println("Generated 5 sensor readings at " + Instant.now());
    }

    private void generateReading(String sensorId, String sensorType, double min, double max, String unit) {
        double value = min + (random.nextDouble() * (max - min));

        //  5% chance of an anomaly - value spikes outside normal range
        if (random.nextDouble() < 0.05) {
            value = max + (random.nextDouble() * max * 0.3);
        }

        value = Math.round(value * 100.0) / 100.0;

        SensorReading reading = new SensorReading(sensorId, sensorType, value, unit, Instant.now());

        try {
            String json = objectMapper.writeValueAsString(reading);
            String topic = "sensors/" + sensorId;
            MqttMessage message = new MqttMessage(json.getBytes());
            mqttClient.publish(topic, message);
        } catch (JsonProcessingException | MqttException e) {
            System.err.println("Failed to publish: " + e.getMessage());
        }
    }
}

package com.energyprojects.scada.service;

import com.energyprojects.scada.model.SensorReading;
import com.energyprojects.scada.repository.SensorReadingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.eclipse.paho.mqttv5.client.IMqttMessageListener;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Service;

@Service
public class MqttSubscriberService {

    private final SensorReadingRepository repository;
    private final AlertService alertService;
    private final ObjectMapper objectMapper;

    public MqttSubscriberService(SensorReadingRepository repository, AlertService alertService) {
        this.repository = repository;
        this.alertService = alertService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @PostConstruct
    public void startListening() throws Exception {
        MqttClient client = new MqttClient("tcp://localhost:1883", "scada-backend");
        MqttConnectionOptions options = new MqttConnectionOptions();
        client.connect(options);

        MqttSubscription subscription = new MqttSubscription("sensors/#", 0);
        IMqttMessageListener listener = (topic, message) -> {
            try {
                String json = new String(message.getPayload());
                SensorReading reading = objectMapper.readValue(json, SensorReading.class);
                repository.save(reading);
                alertService.checkReading(reading);
            } catch (Exception e) {
                System.err.println("Failed to process message: " + e.getMessage());
            }
        };

        client.subscribe(new MqttSubscription[]{subscription}, new IMqttMessageListener[]{listener});

        System.out.println("MQTT subscriber listening on sensors/#");
    }
}
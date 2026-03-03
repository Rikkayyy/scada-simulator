package com.energyprojects.scada.controller;

import com.energyprojects.scada.model.SensorReading;
import com.energyprojects.scada.repository.SensorReadingRepository;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {
    
    private final SensorReadingRepository repository;

    public SensorController(SensorReadingRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/readings")
    public List<SensorReading> getRecentReadings() {
        Instant since = Instant.now().minus(1, ChronoUnit.HOURS);
        return repository.findByTimestampAfterOrderByTimestampDesc(since);
    }

    @GetMapping("/readings/{sensorId}")
    public List<SensorReading> getReadingsBySensor(@PathVariable String sensorId) {
        return repository.findBySensorIdOrderByTimestampDesc(sensorId);
    }

    @GetMapping("/readings/type/{sensortype}")
    public List<SensorReading> getReadingsByType(@PathVariable String sensortype) {
        return repository.findBySensorTypeOrderByTimestampDesc(sensortype);
    }
    
    
}

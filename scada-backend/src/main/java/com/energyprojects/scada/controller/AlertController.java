package com.energyprojects.scada.controller;

import com.energyprojects.scada.model.Alert;
import com.energyprojects.scada.repository.AlertRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertRepository alertRepository;

    public AlertController(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @GetMapping
    public List<Alert> getUnacknowledgedAlerts() {
        return alertRepository.findByAcknowledgedFalseOrderByTimestampDesc();
    }

    @GetMapping("/all")
    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    @PutMapping("/{id}/acknowledge")
    public Alert acknowledgeAlert(@PathVariable Long id) {
        Alert alert = alertRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Alert not found"));
        alert.setAcknowledged(true);
        return alertRepository.save(alert);
    }
}

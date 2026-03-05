package com.energyprojects.scada.repository;

import com.energyprojects.scada.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByAcknowledgedFalseOrderByTimestampDesc();

    List<Alert> findBySensorIdOrderByTimestampDesc(String sensorId);
}
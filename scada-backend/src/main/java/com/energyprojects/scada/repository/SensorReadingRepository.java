package com.energyprojects.scada.repository;

import com.energyprojects.scada.model.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Repository;


@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {


    List<SensorReading> findBySensorIdOrderByTimestampDesc(String sensorId);

    List<SensorReading> findByTimestampAfterOrderByTimestampDesc(Instant since);

    List<SensorReading> findBySensorTypeOrderByTimestampDesc(String sensorType);
}
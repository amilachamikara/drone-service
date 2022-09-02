package org.amila.droneservice.config;

import org.amila.droneservice.dao.DroneDAO;
import org.amila.droneservice.repository.DroneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ScheduleConfig {

    private final Logger logger = LoggerFactory.getLogger("drone.battery-level");

    private DroneRepository droneRepository;

    @Autowired
    public void setDroneRepository(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
    }

    @Scheduled(fixedDelayString = "${drone-battery-check-interval-ms}")
    private void checkBatteryLevels() {
        for (DroneDAO drone : droneRepository.findAll()) {
            logger.info("{}: {}", drone.getSerialNumber(), drone.getBatteryLevel());
        }
    }
}

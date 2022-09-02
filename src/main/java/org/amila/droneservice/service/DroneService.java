package org.amila.droneservice.service;

import org.amila.droneservice.dto.BatteryStatus;
import org.amila.droneservice.dto.Drone;
import org.amila.droneservice.dto.DroneSearchCriteria;
import org.amila.droneservice.dto.Medication;

import java.util.List;

public interface DroneService {
    Drone registerDrone(Drone drone);

    List<Drone> searchDrones(DroneSearchCriteria criteria);

    List<Drone> listAllDrones();

    Drone getDroneBySerialNumber(String serialNumber);

    BatteryStatus getDroneBatteryStatus(String serialNumber);

    List<Medication> getDroneMedications(String droneSerialNumber);

    List<Medication> addMedications(String droneSerialNumber, List<Medication> medications);
}

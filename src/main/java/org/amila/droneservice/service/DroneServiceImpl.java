package org.amila.droneservice.service;

import org.amila.droneservice.common.State;
import org.amila.droneservice.common.exception.LowBatteryException;
import org.amila.droneservice.common.exception.ValidationException;
import org.amila.droneservice.common.exception.WeightLimitException;
import org.amila.droneservice.dao.DroneDAO;
import org.amila.droneservice.dao.MedicationDAO;
import org.amila.droneservice.dto.BatteryStatus;
import org.amila.droneservice.dto.Drone;
import org.amila.droneservice.dto.DroneSearchCriteria;
import org.amila.droneservice.dto.Medication;
import org.amila.droneservice.repository.DroneRepository;
import org.amila.droneservice.service.mapper.DroneMapper;
import org.amila.droneservice.service.mapper.MedicationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class DroneServiceImpl implements DroneService {

    private static final double MIN_BATTERY_FOR_LOADING = 25;

    private final Logger logger = LoggerFactory.getLogger("drone.service");

    private DroneRepository droneRepository;
    private DroneMapper droneMapper;

    private MedicationMapper medicationMapper;

    @Autowired
    public void setDroneRepository(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
    }

    @Autowired
    public void setDroneMapper(DroneMapper droneMapper) {
        this.droneMapper = droneMapper;
    }

    @Autowired
    public void setMedicationMapper(MedicationMapper medicationMapper) {
        this.medicationMapper = medicationMapper;
    }

    @Override
    public Drone registerDrone(Drone drone) {
        DroneDAO dao = droneMapper.mapIn(drone);
        dao.setState(State.LOADING);
        DroneDAO saved = droneRepository.save(dao);
        logger.info("New drone registered: {}", saved);
        return droneMapper.mapOut(saved);
    }

    @Override
    public List<Drone> searchDrones(DroneSearchCriteria criteria) {
        List<DroneDAO> list = droneRepository.findAllByState(criteria.getState());
        logger.info("Searching drones {} --> {} drones found", criteria, list.size());
        return droneMapper.mapListOut(list);
    }

    @Override
    public List<Drone> listAllDrones() {
        List<DroneDAO> list = droneRepository.findAll();
        logger.info("Listing all drones... --> {} drones found", list.size());
        return droneMapper.mapListOut(list);
    }

    @Override
    public Drone getDroneBySerialNumber(String serialNumber) {
        Optional<DroneDAO> droneOptional = droneRepository.findBySerialNumber(serialNumber);
        logger.info("Getting drone: {} --> found: {}", serialNumber, droneOptional.isPresent());
        return droneOptional.map(droneDAO -> droneMapper.mapOut(droneDAO)).orElse(null);
    }

    @Override
    public BatteryStatus getDroneBatteryStatus(String serialNumber) {
        Drone drone = getDroneBySerialNumber(serialNumber);
        if (drone != null) {
            logger.info("Getting battery status of drone {} --> {}", serialNumber, drone.getBatteryLevel());
            BatteryStatus batteryStatus = new BatteryStatus();
            batteryStatus.setSerialNumber(drone.getSerialNumber());
            batteryStatus.setBatteryLevel(drone.getBatteryLevel());
            return batteryStatus;
        }
        logger.warn("Drone not found for {}", serialNumber);
        return null;
    }

    @Override
    public List<Medication> getDroneMedications(String droneSerialNumber) {
        Optional<DroneDAO> droneOptional = droneRepository.findBySerialNumber(droneSerialNumber);
        if (droneOptional.isPresent()) {
            List<MedicationDAO> medications = droneOptional.get().getMedications();
            logger.info("Getting drone medications {} --> {} medications found", droneSerialNumber, medications.size());
            return medicationMapper.mapListOut(medications);
        }
        logger.warn("Drone not found for {}", droneSerialNumber);
        return Collections.emptyList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Override
    public List<Medication> addMedications(String droneSerialNumber, List<Medication> medications) {

        List<MedicationDAO> medicationDAOS = medicationMapper.mapListIn(medications);

        Optional<DroneDAO> droneOptional = droneRepository.findBySerialNumber(droneSerialNumber);
        if (droneOptional.isPresent()) {
            DroneDAO droneDAO = droneOptional.get();

            validate(medicationDAOS, droneDAO);

            droneDAO.getMedications().addAll(medicationDAOS);
            droneDAO.setState(State.LOADED);
            DroneDAO savedDroneDAO = droneRepository.saveAndFlush(droneDAO);
            return medicationMapper.mapListOut(savedDroneDAO.getMedications());
        } else {
            logger.error("Drone not found. {}", droneSerialNumber);
            throw new ValidationException("Specified drone is not found");
        }
    }

    private void validate(List<MedicationDAO> medicationDAOS, DroneDAO droneDAO) throws ValidationException {
        validateStatus(droneDAO);
        validateBatteryLevel(droneDAO);
        validateWeight(medicationDAOS, droneDAO);
    }

    private void validateStatus(DroneDAO droneDAO) {
        if (droneDAO.getState() != State.LOADING) {
            String message = String.format("Drone is not at %s state for loading!", State.LOADING);
            logger.error("{}:{}", droneDAO.getState(), message);
            throw new ValidationException(message);
        }
    }

    private void validateWeight(List<MedicationDAO> medicationDAOS, DroneDAO droneDAO) {
        double currentLoadWeight = calculateLoadWeight(droneDAO.getMedications());
        double newLoadWeight = calculateLoadWeight(medicationDAOS);
        double total = currentLoadWeight + newLoadWeight;

        if (total > droneDAO.getMaxWeight()) {
            String message = String.format("Maximum allowed weight (%.2f) going to be exceeded [Current Load = %.2f, New Load = %.2f]",
                    droneDAO.getMaxWeight(), currentLoadWeight, newLoadWeight);
            logger.error("{}:{}", droneDAO.getState(), message);
            throw new WeightLimitException(message);
        }
    }

    private void validateBatteryLevel(DroneDAO droneDAO) {
        if (droneDAO.getBatteryLevel() < MIN_BATTERY_FOR_LOADING) {
            String message = String.format("Battery level is not sufficient for loading! [Current level = %.2f, Minimum Level = %.2f]",
                    droneDAO.getBatteryLevel(), MIN_BATTERY_FOR_LOADING);
            logger.error("{}:{}", droneDAO.getState(), message);
            throw new LowBatteryException(message);
        }
    }

    private double calculateLoadWeight(List<MedicationDAO> medications) {
        double total = 0.0;

        if (medications != null) {
            for (MedicationDAO medication : medications) {
                total += medication.getWeight();
            }
        }
        return total;
    }

}

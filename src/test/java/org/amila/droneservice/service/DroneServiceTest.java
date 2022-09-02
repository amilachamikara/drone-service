package org.amila.droneservice.service;

import org.amila.droneservice.common.Model;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DroneServiceTest {

    @Mock
    private DroneRepository droneRepository;

    private DroneService droneService;

    List<DroneDAO> drones = Arrays.asList(
            new DroneDAO("SN123", Model.LIGHT_WEIGHT, 100.0, 40.0, State.LOADING, Arrays.asList(
                    new MedicationDAO("B705", "Naproxen", 50.0, "https://www.drugs.com/images/pills/fio/IPL01901/naproxen.JPG")
            )),
            new DroneDAO("SN150", Model.MIDDLE_WEIGHT, 200.0, 70.0, State.LOADED, Collections.emptyList()),
            new DroneDAO("SN170", Model.MIDDLE_WEIGHT, 200.0, 20.0, State.LOADING, Collections.emptyList())
    );

    @BeforeEach
    void init() {
        DroneServiceImpl droneServiceImpl = new DroneServiceImpl();
        droneServiceImpl.setDroneRepository(droneRepository);
        droneServiceImpl.setDroneMapper(Mappers.getMapper(DroneMapper.class));
        droneServiceImpl.setMedicationMapper(Mappers.getMapper(MedicationMapper.class));
        this.droneService = droneServiceImpl;
    }


    @Test
    void addMedicationsInvalidStateTest() {
        when(droneRepository.findBySerialNumber(drones.get(1).getSerialNumber())).thenReturn(drones.stream().filter(droneDAO -> droneDAO.getSerialNumber().equals(drones.get(1).getSerialNumber())).findFirst());

        assertThrows(ValidationException.class, () -> {
            droneService.addMedications(drones.get(1).getSerialNumber(), Arrays.asList(new Medication("A", 100.0, "A", "A")));
        });
    }

    @Test
    void addMedicationsHighWeightTest() {
        when(droneRepository.findBySerialNumber(drones.get(0).getSerialNumber())).thenReturn(drones.stream().filter(droneDAO -> droneDAO.getSerialNumber().equals(drones.get(0).getSerialNumber())).findFirst());

        assertThrows(WeightLimitException.class, () -> {
            droneService.addMedications(drones.get(0).getSerialNumber(), Arrays.asList(new Medication("A", 100.0, "A", "A")));
        });
    }

    @Test
    void addMedicationsLowBatteryLevelTest() {
        when(droneRepository.findBySerialNumber(drones.get(2).getSerialNumber())).thenReturn(drones.stream().filter(droneDAO -> droneDAO.getSerialNumber().equals(drones.get(2).getSerialNumber())).findFirst());

        assertThrows(LowBatteryException.class, () -> droneService.addMedications(drones.get(2).getSerialNumber(), Arrays.asList(new Medication("A", 100.0, "A", "A"))));
    }

    @Test
    void registerDrone() {
        when(droneRepository.save(any(DroneDAO.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

        Drone saved = droneService.registerDrone(new Drone("SN123", Model.MIDDLE_WEIGHT, 500.0, 100.0, State.LOADING));
        assertEquals("SN123", saved.getSerialNumber());
        assertEquals(Model.MIDDLE_WEIGHT, saved.getModel());
        assertEquals(500.0, saved.getMaxWeight());
        assertEquals(100.0, saved.getBatteryLevel());
        assertEquals(State.LOADING, saved.getState());
    }

    @Test
    void searchDrones() {

        when(droneRepository.findAllByState(State.LOADING)).thenReturn(drones.stream().filter(droneDAO -> droneDAO.getState().equals(State.LOADING)).collect(Collectors.toList()));

        List<Drone> result = droneService.searchDrones(new DroneSearchCriteria(State.LOADING));

        assertEquals(2, result.size());
        assertEquals(drones.get(0).getSerialNumber(), result.get(0).getSerialNumber());
        assertEquals(drones.get(0).getState(), result.get(0).getState());
        assertEquals(drones.get(0).getMaxWeight(), result.get(0).getMaxWeight());
        assertEquals(drones.get(0).getBatteryLevel(), result.get(0).getBatteryLevel());
    }

    @Test
    void listAllDrones() {

        when(droneRepository.findAll()).thenReturn(drones);

        List<Drone> result = droneService.listAllDrones();

        assertEquals(drones.size(), result.size());
        assertEquals(drones.get(0).getSerialNumber(), result.get(0).getSerialNumber());
        assertEquals(drones.get(0).getState(), result.get(0).getState());
        assertEquals(drones.get(0).getMaxWeight(), result.get(0).getMaxWeight());
        assertEquals(drones.get(0).getBatteryLevel(), result.get(0).getBatteryLevel());

        assertEquals(drones.size(), result.size());
        assertEquals(drones.get(1).getSerialNumber(), result.get(1).getSerialNumber());
        assertEquals(drones.get(1).getState(), result.get(1).getState());
        assertEquals(drones.get(1).getMaxWeight(), result.get(1).getMaxWeight());
        assertEquals(drones.get(1).getBatteryLevel(), result.get(1).getBatteryLevel());
    }

    @Test
    void getDroneBySerialNumber() {
        when(droneRepository.findBySerialNumber(drones.get(0).getSerialNumber())).thenReturn(drones.stream().filter(droneDAO -> droneDAO.getSerialNumber().equals(drones.get(0).getSerialNumber())).findFirst());

        Drone result = droneService.getDroneBySerialNumber(drones.get(0).getSerialNumber());

        assertNotNull(result);
        assertEquals(drones.get(0).getSerialNumber(), result.getSerialNumber());
        assertEquals(drones.get(0).getState(), result.getState());
        assertEquals(drones.get(0).getMaxWeight(), result.getMaxWeight());
        assertEquals(drones.get(0).getBatteryLevel(), result.getBatteryLevel());
    }

    @Test
    void getDroneBatteryStatus() {
        when(droneRepository.findBySerialNumber(drones.get(0).getSerialNumber())).thenReturn(drones.stream().filter(droneDAO -> droneDAO.getSerialNumber().equals(drones.get(0).getSerialNumber())).findFirst());

        BatteryStatus result = droneService.getDroneBatteryStatus(drones.get(0).getSerialNumber());

        assertNotNull(result);
        assertEquals(drones.get(0).getSerialNumber(), result.getSerialNumber());
        assertEquals(drones.get(0).getBatteryLevel(), result.getBatteryLevel());
    }

    @Test
    void getDroneMedications() {
        when(droneRepository.findBySerialNumber(drones.get(0).getSerialNumber())).thenReturn(drones.stream().filter(droneDAO -> droneDAO.getSerialNumber().equals(drones.get(0).getSerialNumber())).findFirst());

        List<Medication> results = droneService.getDroneMedications(drones.get(0).getSerialNumber());

        assertNotNull(results);
        assertEquals(1, results.size());
        assertNotNull(results.get(0));
        assertEquals(drones.get(0).getMedications().get(0).getCode(), results.get(0).getCode());
        assertEquals(drones.get(0).getMedications().get(0).getName(), results.get(0).getName());
        assertEquals(drones.get(0).getMedications().get(0).getWeight(), results.get(0).getWeight());
        assertEquals(drones.get(0).getMedications().get(0).getImageURL(), results.get(0).getImageURL());
    }
}
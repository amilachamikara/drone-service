package org.amila.droneservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.amila.droneservice.common.Response;
import org.amila.droneservice.common.State;
import org.amila.droneservice.common.Status;
import org.amila.droneservice.dto.BatteryStatus;
import org.amila.droneservice.dto.Drone;
import org.amila.droneservice.dto.DroneSearchCriteria;
import org.amila.droneservice.dto.Medication;
import org.amila.droneservice.service.DroneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(
        value = "api/v1/drones",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Tag(name = "drones")
public class DroneController {
    private DroneService droneService;

    @Autowired
    public void setDroneService(DroneService droneService) {
        this.droneService = droneService;
    }

    @PostMapping
    @Operation(summary = "Registering a drone")
    public ResponseEntity<Response<Drone>> registerDrone(@RequestBody Drone drone) {
        Drone savedDrone = droneService.registerDrone(drone);
        Response<Drone> droneResponse = new Response<>(savedDrone, Status.SUCCESS);
        return ResponseEntity.ok(droneResponse);
    }

    @GetMapping
    @Operation(summary = "Searching drones")
    public ResponseEntity<Response<List<Drone>>> searchDrones(@RequestParam(value = "state", required = false) State state) {

        if (Objects.nonNull(state)) {
            List<Drone> filteredDrones = droneService.searchDrones(new DroneSearchCriteria(state));
            Response<List<Drone>> response = new Response<>(filteredDrones, Status.SUCCESS);
            return ResponseEntity.ok(response);
        }
        List<Drone> allDrones = droneService.listAllDrones();
        Response<List<Drone>> response = new Response<>(allDrones, Status.SUCCESS);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "{serialNumber}")
    @Operation(summary = "Getting drone details")
    public ResponseEntity<Response<Drone>> getDrone(@PathVariable String serialNumber) {
        Drone drone = droneService.getDroneBySerialNumber(serialNumber);
        Response<Drone> droneResponse = new Response<>(drone, Status.SUCCESS);
        return ResponseEntity.ok(droneResponse);
    }

    @GetMapping(value = "{serialNumber}/battery")
    @Operation(summary = "Checking battery level for a given drone")
    public ResponseEntity<Response<BatteryStatus>> getDroneBatteryStatus(@PathVariable("serialNumber") String serialNumber) {
        BatteryStatus batteryStatus = droneService.getDroneBatteryStatus(serialNumber);
        Response<BatteryStatus> batteryStatusResponse = new Response<>(batteryStatus, Status.SUCCESS);
        return ResponseEntity.ok(batteryStatusResponse);
    }

    @PostMapping(value = "{serialNumber}/medications")
    @Operation(summary = "Loading a drone with medication items")
    public ResponseEntity<Response<List<Medication>>> addMedications(@PathVariable("serialNumber") String droneSerialNumber, @RequestBody List<Medication> medications) {
        List<Medication> savedMedications = droneService.addMedications(droneSerialNumber, medications);
        Response<List<Medication>> savedMedicationsResponse = new Response<>(savedMedications, Status.SUCCESS);
        return ResponseEntity.ok(savedMedicationsResponse);
    }

    @GetMapping(value = "{droneSerialNumber}/medications")
    @Operation(summary = "Checking loaded medication items for a given drone")
    public ResponseEntity<Response<List<Medication>>> getMedications(@PathVariable("droneSerialNumber") String droneSerialNumber) {
        List<Medication> medications = droneService.getDroneMedications(droneSerialNumber);
        Response<List<Medication>> medicationsResponse = new Response<>(medications, Status.SUCCESS);
        return ResponseEntity.ok(medicationsResponse);
    }
}

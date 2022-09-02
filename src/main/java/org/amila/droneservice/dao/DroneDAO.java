package org.amila.droneservice.dao;


import org.amila.droneservice.common.Model;
import org.amila.droneservice.common.State;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "DS_DRONE")
public class DroneDAO extends AbstractAuditableEntity {
    @Column(name = "SN", unique = true, length = 100, nullable = false)
    private String serialNumber;

    @Column(name = "MODEL", nullable = false)
    @Enumerated(EnumType.STRING)
    private Model model;

    @Column(name = "MAX_WEIGHT")
    private Double maxWeight;

    @Column(name = "BATTERY_LEVEL")
    private Double batteryLevel;

    @Column(name = "STATE", nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "DRONE_ID")
    private List<MedicationDAO> medications;

    public DroneDAO() {
    }

    public DroneDAO(String serialNumber, Model model, Double maxWeight, Double batteryLevel, State state, List<MedicationDAO> medications) {
        this.serialNumber = serialNumber;
        this.model = model;
        this.maxWeight = maxWeight;
        this.batteryLevel = batteryLevel;
        this.state = state;
        this.medications = medications;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(Double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public Double getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Double batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public List<MedicationDAO> getMedications() {
        return medications;
    }

    public void setMedications(List<MedicationDAO> medications) {
        this.medications = medications;
    }

    @Override
    public String toString() {
        return "DroneDAO{" +
                "serialNumber='" + serialNumber + '\'' +
                ", model=" + model +
                ", maxWeight=" + maxWeight +
                ", batteryLevel=" + batteryLevel +
                '}';
    }
}

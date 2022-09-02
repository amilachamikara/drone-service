package org.amila.droneservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.amila.droneservice.common.Model;
import org.amila.droneservice.common.State;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Drone {

    @JsonProperty("serialNumber")
    private String serialNumber;

    @JsonProperty("model")
    private Model model;

    @JsonProperty("maxWeight")
    private Double maxWeight;

    @JsonProperty("batteryLevel")
    private Double batteryLevel;

    @JsonProperty(value = "state", access = JsonProperty.Access.READ_ONLY)
    private State state;

    public Drone() {
    }

    public Drone(String serialNumber, Model model, Double maxWeight, Double batteryLevel, State state) {
        this.serialNumber = serialNumber;
        this.model = model;
        this.maxWeight = maxWeight;
        this.batteryLevel = batteryLevel;
        this.state = state;
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
}

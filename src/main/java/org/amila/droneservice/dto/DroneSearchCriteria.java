package org.amila.droneservice.dto;

import org.amila.droneservice.common.State;

public class DroneSearchCriteria {
    private State state;

    public DroneSearchCriteria(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "DroneSearchCriteria{" +
                "state=" + state +
                '}';
    }
}

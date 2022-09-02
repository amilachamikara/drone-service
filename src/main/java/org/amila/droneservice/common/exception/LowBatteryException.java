package org.amila.droneservice.common.exception;

public class LowBatteryException extends ValidationException {
    public LowBatteryException(String message) {
        super(message);
    }
}

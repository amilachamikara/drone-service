package org.amila.droneservice.common;

public enum Status {
    ERROR(-1),
    WARNING(0),
    SUCCESS(1);

    private final int code;

    Status(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

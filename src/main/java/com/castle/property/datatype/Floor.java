package com.castle.property.datatype;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Floor {
    FLOOR_Ground("Floor Ground"),
    FLOOR_1("Floor 1"),
    FLOOR_2("Floor 2"),
    FLOOR_3("Floor 3"),
    FLOOR_4("Floor 4"),
    FLOOR_5("Floor 5"),
    FLOOR_6("Floor 6"),
    FLOOR_7("Floor 7"),
    FLOOR_8("Floor 8"),
    FLOOR_9("Floor 9");

    public final String label;

    Floor(String label) {
        this.label = label;
    }

    @Override
    @JsonValue
    public String toString() {
        return label;
    }

    @JsonCreator
    public static Floor forValue(String value) {
        return switch (value.toUpperCase()) {
            case "FLOOR GROUND" -> FLOOR_Ground;
            case "FLOOR 1" -> FLOOR_1;
            case "FLOOR 2" -> FLOOR_2;
            case "FLOOR 3" -> FLOOR_3;
            case "FLOOR 4" -> FLOOR_4;
            case "FLOOR 5" -> FLOOR_5;
            case "FLOOR 6" -> FLOOR_6;
            case "FLOOR 7" -> FLOOR_7;
            case "FLOOR 8" -> FLOOR_8;
            case "FLOOR 9" -> FLOOR_9;
            default -> throw new IllegalArgumentException(String.format("illegal value %s", value));
        };
    }

    public String getLabel() {
        return label;
    }
}

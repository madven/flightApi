package com.tokigames.selcukc.enums;

import java.util.Arrays;

public enum OrderBy {
    DEPARTURE("departure"),
    DEPARTURE_TIME("departureTime"),
    ARRIVAL("arrival"),
    ARRIVAL_TIME("arrivalTime");

    private String text;

    OrderBy(String text) {
        this.text = text;
    }

    public static OrderBy fromString(String text) {
        return Arrays.stream(OrderBy.values()).filter(o -> o.text.equalsIgnoreCase(text)).findFirst().orElse(null);
    }

}
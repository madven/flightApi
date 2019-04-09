package com.tokigames.selcukc.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Flight {

    private String departure;
    private String arrival;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
}

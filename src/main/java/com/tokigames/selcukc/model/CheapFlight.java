package com.tokigames.selcukc.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tokigames.selcukc.helper.LocalDateTimeFromEpochDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheapFlight {

    @JsonDeserialize
    private Long id;
    @JsonDeserialize
    private String departure;
    @JsonDeserialize
    private String arrival;

    @JsonDeserialize(using = LocalDateTimeFromEpochDeserializer.class)
    private LocalDateTime departureTime;

    @JsonDeserialize(using = LocalDateTimeFromEpochDeserializer.class)
    private LocalDateTime arrivalTime;

}

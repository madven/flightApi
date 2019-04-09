package com.tokigames.selcukc.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessFlight {

    @JsonDeserialize
    private UUID uuid;
    @JsonDeserialize
    private String flight;

    @JsonDeserialize
    private LocalDateTime departure;

    @JsonDeserialize
    private LocalDateTime arrival;
}

package com.tokigames.selcukc.controller;

import com.tokigames.selcukc.enums.OrderBy;
import com.tokigames.selcukc.model.Flight;
import com.tokigames.selcukc.service.FlightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@Slf4j
public class FlightController {

    private final FlightService flightService;

    @Autowired
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    // Time parameters should be in milliseconds
    @GetMapping(path = "/flights", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Flight>> getFligths(@RequestParam(value = "departure") Optional<String> departure,
                                                  @RequestParam(value = "departureBefore") Optional<Long> departureBefore,
                                                  @RequestParam(value = "departureAfter") Optional<Long> departureAfter,
                                                  @RequestParam(value = "arrival") Optional<String> arrival,
                                                  @RequestParam(value = "arrivalBefore") Optional<Long> arrivalBefore,
                                                  @RequestParam(value = "arrivalAfter") Optional<Long> arrivalAfter,
                                                  @PageableDefault(sort = "departureTime") Pageable pageable){
        log.info("Flights requested. Paramters: "
                + (departure.map(d -> "departure: " + d + ", ").orElse(""))
                + (departureBefore.map(d -> "departureBefore: " + d + ", ").orElse(""))
                + (departureAfter.map(d -> "departureAfter: " + d + ", ").orElse(""))
                + (arrival.map(a -> "arrival: " + a + ", ").orElse(""))
                + (arrivalBefore.map(d -> "arrivalBefore: " + d + ", ").orElse(""))
                + (arrivalAfter.map(d -> "arrivalAfter: " + d + ", ").orElse(""))
                + "pageable: " + pageable
                + "."
        );

        // Check if sort parameters are provided properly
        boolean isInvalidSortByCondition = pageable.getSort().get().anyMatch(o -> OrderBy.fromString(o.getProperty()) == null);
        if(isInvalidSortByCondition)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sort condition");

        Page<Flight> flights = flightService.getFligthsByParams(departure, departureBefore, departureAfter,
                arrival, arrivalBefore, arrivalAfter, pageable);

        return flights.getNumberOfElements() > 0
                ? new ResponseEntity<>(flights, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

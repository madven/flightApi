package com.tokigames.selcukc.service;

import com.tokigames.selcukc.enums.OrderBy;
import com.tokigames.selcukc.helper.Utils;
import com.tokigames.selcukc.model.BusinessFlight;
import com.tokigames.selcukc.model.CheapFlight;
import com.tokigames.selcukc.model.Flight;
import com.tokigames.selcukc.repository.BusinessRepository;
import com.tokigames.selcukc.repository.CheapRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class FlightService {

    private final CheapRepository cheapRepository;
    private final BusinessRepository businessRepository;

    @Autowired
    public FlightService(CheapRepository cheapRepository, BusinessRepository businessRepository) {
        this.cheapRepository = cheapRepository;
        this.businessRepository = businessRepository;
    }

    public Page<Flight> getFligthsByParams(Optional<String> departure, Optional<Long> departureBefore, Optional<Long> departureAfter,
                                           Optional<String> arrival, Optional<Long> arrivalBefore, Optional<Long> arrivalAfter,
                                           Pageable pageable) {
        List<Flight> flights = unifyFlights();
        flights = filterFlights(flights, departure, departureBefore, departureAfter, arrival, arrivalBefore, arrivalAfter);
        flights = sortFlights(flights, pageable.getSort());

        return pageFlights(pageable, flights);
    }

    private List<Flight> unifyFlights() {

        CompletableFuture<List<Flight>> cheapFuture = CompletableFuture.supplyAsync(() -> {
            List<CheapFlight> cheapFlights = cheapRepository.fetchFlights();
            log.info("Cheap flights size: " + cheapFlights.size());
            return cheapToFlight(cheapFlights);
        });
        CompletableFuture<List<Flight>> businessFuture = CompletableFuture.supplyAsync(() -> {
            List<BusinessFlight> businessFlights = businessRepository.fetchFlights();
            log.info("Business flights size: " + businessFlights.size());
            return businessToFlight(businessFlights);
        });

        return Stream.of(cheapFuture, businessFuture)
                .flatMap(completableFutures -> completableFutures.join().stream())
                .collect(Collectors.toList());
    }

    private List<Flight> cheapToFlight(List<CheapFlight> cheapFlights) {
        List<Flight> flights = new ArrayList<>();
        Flight flight;
        for (CheapFlight cheapFlight : cheapFlights) {
            flight = new Flight();
            flight.setDeparture(cheapFlight.getDeparture());
            flight.setArrival(cheapFlight.getArrival());
            flight.setDepartureTime(cheapFlight.getDepartureTime());
            flight.setArrivalTime(cheapFlight.getArrivalTime());
            flights.add(flight);
        }
        return flights;
    }

    private List<Flight> businessToFlight(List<BusinessFlight> businessFlights) {
        List<Flight> flights = new ArrayList<>();
        Flight flight;
        for (BusinessFlight businessFlight : businessFlights) {
            flight = new Flight();
            String[] departureArrival = businessFlight.getFlight().trim().split(" -> ");
            flight.setDeparture(departureArrival[0]);
            flight.setArrival(departureArrival[1]);
            flight.setDepartureTime(businessFlight.getDeparture());
            flight.setArrivalTime(businessFlight.getArrival());
            flights.add(flight);
        }
        return flights;
    }

    public List<Flight> filterFlights(List<Flight> flights,
                                       Optional<String> departure, Optional<Long> departureBefore, Optional<Long> departureAfter,
                                       Optional<String> arrival, Optional<Long> arrivalBefore, Optional<Long> arrivalAfter) {
        if (departure.isPresent()) {
            flights = flights.stream().filter(f -> f.getDeparture().equals(departure.get())).collect(Collectors.toList());
        }
        if (departureBefore.isPresent()) {
            flights = flights.stream()
                    .filter(f -> f.getDepartureTime().isBefore(Utils.epochToLocalDateTime(departureBefore.get())))
                    .collect(Collectors.toList());
        }
        if (departureAfter.isPresent()) {
            flights = flights.stream()
                    .filter(f -> f.getDepartureTime().isAfter(Utils.epochToLocalDateTime(departureAfter.get())))
                    .collect(Collectors.toList());
        }
        if (arrival.isPresent()) {
            flights = flights.stream().filter(f -> f.getArrival().equals(arrival.get())).collect(Collectors.toList());
        }
        if (arrivalBefore.isPresent()) {
            flights = flights.stream()
                    .filter(f -> f.getArrivalTime().isBefore(Utils.epochToLocalDateTime(arrivalBefore.get())))
                    .collect(Collectors.toList());
        }
        if (arrivalAfter.isPresent()) {
            flights = flights.stream()
                    .filter(f -> f.getArrivalTime().isAfter(Utils.epochToLocalDateTime(arrivalAfter.get())))
                    .collect(Collectors.toList());
        }

        return flights;
    }

    private List<Flight> sortFlights(List<Flight> flights, Sort sort) {
        Comparator<Flight> comparator = Comparator.comparing(Flight::getDepartureTime);

        List<Sort.Order> orders = sort.get().collect(Collectors.toList());
        for (int i = 0; i < orders.size(); i++) {
            Sort.Order order = orders.get(i);
            OrderBy orderBy = OrderBy.fromString(order.getProperty());
            if(orderBy != null){
                switch (orderBy) {
                    case DEPARTURE:
                        comparator = i == 0 ? Comparator.comparing(Flight::getDeparture) : comparator.thenComparing(Flight::getDeparture);
                        break;
                    case DEPARTURE_TIME:
                        comparator = i == 0 ? Comparator.comparing(Flight::getDepartureTime) : comparator.thenComparing(Flight::getDepartureTime);
                        break;
                    case ARRIVAL:
                        comparator = i == 0 ? Comparator.comparing(Flight::getArrival) : comparator.thenComparing(Flight::getArrival);
                        break;
                    case ARRIVAL_TIME:
                        comparator = i == 0 ? Comparator.comparing(Flight::getArrivalTime) : comparator.thenComparing(Flight::getArrivalTime);
                        break;
                }

                if (order.getDirection().isDescending()) {
                    comparator = comparator.reversed();
                }
            } else {
                System.out.println("ex");
            }
        }

        return flights.stream().sorted(comparator).collect(Collectors.toList());
    }

    private Page<Flight> pageFlights(Pageable pageable, List<Flight> flights) {
        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > flights.size() ? flights.size() : (start + pageable.getPageSize());

        if (start > flights.size())
            return new PageImpl<>(new ArrayList<>(), pageable, flights.size());
        return new PageImpl<>(flights.subList(start, end), pageable, flights.size());
    }
}

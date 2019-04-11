package com.tokigames.selcukc;

import com.tokigames.selcukc.model.Flight;
import com.tokigames.selcukc.service.FlightService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FlightApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FlightService flightService;

    private List<Flight> flights;

    @Before
    public void init() {
        flights = Arrays.asList(
                new Flight("Ankara", "Istanbul", LocalDateTime.now(), LocalDateTime.now()),
                new Flight("Ankara", "Bursa", LocalDateTime.now(), LocalDateTime.now()),
                new Flight("Bursa", "Istanbul", LocalDateTime.now(), LocalDateTime.now())
        );
    }

    @Test
    public void whenGetFlights_thenStatus200() throws Exception {
        mockMvc.perform(get("/api/flights"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void whenFilteringFlights() {

        List<Flight> filteredFlights = flightService.filterFlights(flights,
                Optional.of("Ankara"), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty());

        assertThat(filteredFlights.size(), is(2));

        filteredFlights = flightService.filterFlights(flights,
                Optional.of("Ankara"), Optional.empty(), Optional.empty(),
                Optional.of("Istanbul"), Optional.empty(), Optional.empty());

        assertThat(filteredFlights.size(), is(1));

    }

}

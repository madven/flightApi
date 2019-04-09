package com.tokigames.selcukc.repository;

import com.tokigames.selcukc.model.CheapFlight;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class CheapRepository {

    public List<CheapFlight> fetchFlights(){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<CheapFlight>> response = restTemplate.exchange(
                "https://obscure-caverns-79008.herokuapp.com/cheap",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CheapFlight>>(){});
        return response.getBody();
    }

}

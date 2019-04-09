package com.tokigames.selcukc.repository;

import com.tokigames.selcukc.model.BusinessFlight;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class BusinessRepository {

    public List<BusinessFlight> fetchFlights(){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<BusinessFlight>> response = restTemplate.exchange(
                "https://obscure-caverns-79008.herokuapp.com/business",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BusinessFlight>>(){});
        return response.getBody();
    }
}

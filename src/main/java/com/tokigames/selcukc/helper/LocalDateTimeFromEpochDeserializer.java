package com.tokigames.selcukc.helper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalDateTimeFromEpochDeserializer extends StdDeserializer<LocalDateTime> {

    private static final long serialVersionUID = 1L;

    protected LocalDateTimeFromEpochDeserializer() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        return Instant.ofEpochMilli(jp.readValueAs(Long.class)).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}
package com.tokigames.selcukc.helper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Utils {
    public static LocalDateTime epochToLocalDateTime(Long epoch){
        return Instant.ofEpochMilli(epoch).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}

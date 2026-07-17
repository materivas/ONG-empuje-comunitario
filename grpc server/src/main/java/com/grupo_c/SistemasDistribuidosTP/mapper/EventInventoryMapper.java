package com.grupo_c.SistemasDistribuidosTP.mapper;

import com.grupo_c.SistemasDistribuidosTP.entity.Event;
import com.grupo_c.SistemasDistribuidosTP.entity.EventInventory;
import com.grupo_c.SistemasDistribuidosTP.service.EventServiceClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class EventInventoryMapper {

    public EventServiceClass.DonationDto toDonationDto(EventInventory eventInventory){
        return EventServiceClass.DonationDto.newBuilder()
                .setUsername(eventInventory.getUser().getUsername())
                .setDescription(eventInventory.getInventory().getDescription())
                .setQuantity(eventInventory.getQuantityDistributed())
                .setDistributionDate(toTimestamp(eventInventory.getDistributionDate()))
                .build();
    }

    // convierte localdatetime a timestamp
    public com.google.protobuf.Timestamp toTimestamp(LocalDateTime t){
        Instant instant = t.toInstant(ZoneOffset.UTC);

        return com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}

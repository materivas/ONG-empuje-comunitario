package com.grupo_c.SistemasDistribuidosTP.mapper;

import com.grupo_c.SistemasDistribuidosTP.entity.Event;
import com.grupo_c.SistemasDistribuidosTP.entity.User;
import com.grupo_c.SistemasDistribuidosTP.service.EventServiceClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EventMapper {

    public EventServiceClass.EventWithoutParticipantsDto toEventWithoutParticipantsDto (Event e, boolean joined){

        return EventServiceClass.EventWithoutParticipantsDto.newBuilder()
                .setId(e.getId())
                .setName(e.getName())
                .setDescription(e.getDescription())
                .setDate(toTimestamp(e.getDate()))
                .setIsCompleted(e.getIsCompleted())
                .setJoined(joined)
                .build();
    }

    public EventServiceClass.EventWithParticipantsDto toEventWithParticipantsDto (Event e){

        List<User> participants = new ArrayList<>(e.getParticipants());
        List<EventServiceClass.ParticipantDto> participantsDto = new ArrayList<>();

        for(User user: participants){
            participantsDto.add(toParticipantDto(user));
        }

        return EventServiceClass.EventWithParticipantsDto.newBuilder()
                .setId(e.getId())
                .setName(e.getName())
                .setDescription(e.getDescription())
                .setDate(toTimestamp(e.getDate()))
                .setIsCompleted(e.getIsCompleted())
                .addAllParticipants(participantsDto)
                .build();
    }

    public EventServiceClass.ParticipantDto toParticipantDto(User u){
        return EventServiceClass.ParticipantDto.newBuilder()
                .setId(u.getId())
                .setUsername(u.getUsername())
                .build();
    }

    public Event toEntity(EventServiceClass.EventDto dto){
        Event e = new Event();

        e.setName(dto.getName());
        e.setDescription(dto.getDescription());
        e.setDate(toLocalDateTime(dto.getDate()));
        e.setIsCompleted(false);

        return e;
    }

    public Event toEntityWithParticipants(EventServiceClass.EventWithParticipantsDto dto, Set<User> participants){
        Event e = new Event();

        e.setId(dto.getId());
        e.setName(dto.getName());
        e.setDescription(dto.getDescription());
        e.setDate(toLocalDateTime(dto.getDate()));
        e.setIsCompleted(dto.getIsCompleted());
        e.setParticipants(participants);

        return e;
    }

    // convierte timestamp a localdatetime
    public LocalDateTime toLocalDateTime(com.google.protobuf.Timestamp t){
        Instant instant = Instant.ofEpochSecond(t.getSeconds(), t.getNanos());
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
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

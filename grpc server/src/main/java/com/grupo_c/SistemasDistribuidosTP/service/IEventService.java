package com.grupo_c.SistemasDistribuidosTP.service;

import com.grupo_c.SistemasDistribuidosTP.entity.Event;
import com.grupo_c.SistemasDistribuidosTP.entity.User;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface IEventService {
    void removeUserFromUpcomingEvents(User user);
    void markPastEventsAsCompleted();
    void markPastEventsAsCompletedWithDateTime(LocalDateTime dateTime);
    Event findById(Long id);
    Event findByIdJoinParticipants(Long eventId);
    Event findByIdJoinEventInventory(Long eventId);

}

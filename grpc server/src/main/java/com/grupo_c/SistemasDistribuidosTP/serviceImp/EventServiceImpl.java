package com.grupo_c.SistemasDistribuidosTP.serviceImp;

import com.grupo_c.SistemasDistribuidosTP.entity.Event;
import com.grupo_c.SistemasDistribuidosTP.entity.EventInventory;
import com.grupo_c.SistemasDistribuidosTP.entity.User;
import com.grupo_c.SistemasDistribuidosTP.mapper.EventMapper;
import com.grupo_c.SistemasDistribuidosTP.repository.IEventInventoryRepository;
import com.grupo_c.SistemasDistribuidosTP.repository.IEventRepository;
import com.grupo_c.SistemasDistribuidosTP.repository.IUserRepository;
import com.grupo_c.SistemasDistribuidosTP.service.IEventInventoryService;
import com.grupo_c.SistemasDistribuidosTP.service.IEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventServiceImpl implements IEventService, IEventInventoryService {

    private final IEventRepository eventRepository;
    private final IUserRepository userRepository;
    private final IEventInventoryRepository eventInventoryRepository;
    private final EventMapper eventMapper;

    @Autowired
    public EventServiceImpl(
            IEventRepository eventRepository,
            IEventInventoryRepository eventInventoryRepository,
            IUserRepository userRepository
    ) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventInventoryRepository = eventInventoryRepository;
        this.eventMapper = new EventMapper();
    }

    @Override
    public void removeUserFromUpcomingEvents(User user) {
        List<Event> events = eventRepository.findUpcomingEventsByParticipant(user.getId());

        for(Event e: events){
            e.getParticipants().removeIf(p->p.getId().equals(user.getId()));
            eventRepository.save(e);
        }
    }

    @Override
    public void markPastEventsAsCompleted() {
        eventRepository.markPastEventsAsCompleted();
    }

    @Override
    public void markPastEventsAsCompletedWithDateTime(LocalDateTime dateTime) {
        eventRepository.markPastEventsAsCompletedWithDateTime(dateTime);
    }

    @Override
    public Event findById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    @Override
    public Event findByIdJoinParticipants(Long eventId) {
        return eventRepository.findByIdJoinParticipants(eventId).orElse(null);
    }

    @Override
    public Event findByIdJoinEventInventory(Long eventId) {
        return eventRepository.findByIdJoinEventInventory(eventId);
    }


    @Override
    public List<EventInventory> findByEventId(Long eventId) {
        return eventInventoryRepository.findByEventId(eventId);
    }

    @Override
    public List<EventInventory> findByEventIdOrdered(Long eventId) {
        return eventInventoryRepository.findByEventIdOrdered(eventId);
    }
}

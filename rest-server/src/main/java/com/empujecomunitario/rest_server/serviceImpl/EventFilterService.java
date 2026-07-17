package com.empujecomunitario.rest_server.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.empujecomunitario.rest_server.dto.EventFilterRequest;
import com.empujecomunitario.rest_server.dto.EventFilterResponse;
import com.empujecomunitario.rest_server.entity.EventFilter;
import com.empujecomunitario.rest_server.repository.IEventFilterRepository;
import com.empujecomunitario.rest_server.service.IEventFilterService;

@Component
public class EventFilterService implements IEventFilterService {

    private final IEventFilterRepository repository;

    public EventFilterService(IEventFilterRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<EventFilterResponse> getFiltersByUser(Long userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(EventFilterResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public EventFilterResponse getFilterById(Long id) {
        EventFilter filter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Filtro no encontrado con ID: " + id));
        return new EventFilterResponse(filter);
    }

    @Override
    public List<EventFilterResponse> getAllFilters() {
        return repository.findAll()
                .stream()
                .map(EventFilterResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public EventFilterResponse createFilter(EventFilterRequest request) {
        EventFilter filter = new EventFilter(
                request.getUserId(),
                request.getFilterName(),
                request.getCriteriaJson());
        EventFilter savedFilter = repository.save(filter);
        return new EventFilterResponse(savedFilter);
    }

    @Override
    public EventFilterResponse updateFilter(Long id, EventFilterRequest request) {
        EventFilter existingFilter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Filtro no encontrado con ID: " + id));

        existingFilter.setFilterName(request.getFilterName());
        existingFilter.setCriteriaJson(request.getCriteriaJson());

        EventFilter updatedFilter = repository.save(existingFilter);
        return new EventFilterResponse(updatedFilter);
    }

    @Override
    public void deleteFilter(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Filtro no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }

}

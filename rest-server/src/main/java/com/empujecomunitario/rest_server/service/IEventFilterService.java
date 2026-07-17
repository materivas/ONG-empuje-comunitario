package com.empujecomunitario.rest_server.service;

import org.springframework.stereotype.Service;

import com.empujecomunitario.rest_server.dto.EventFilterRequest;
import com.empujecomunitario.rest_server.dto.EventFilterResponse;

import java.util.List;

@Service
public interface IEventFilterService {
    List<EventFilterResponse> getFiltersByUser(Long userId);

    EventFilterResponse getFilterById(Long id);

    List<EventFilterResponse> getAllFilters();

    EventFilterResponse createFilter(EventFilterRequest request);

    EventFilterResponse updateFilter(Long id, EventFilterRequest request);

    void deleteFilter(Long id);
}

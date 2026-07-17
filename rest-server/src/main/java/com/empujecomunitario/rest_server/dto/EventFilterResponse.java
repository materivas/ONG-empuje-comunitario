package com.empujecomunitario.rest_server.dto;

import java.time.LocalDateTime;

import com.empujecomunitario.rest_server.entity.EventFilter;

public class EventFilterResponse {
    private Long id;
    private String filterName;
    private String criteriaJson;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor from Entity
    public EventFilterResponse(EventFilter eventFilter) {
        this.id = eventFilter.getId();
        this.filterName = eventFilter.getFilterName();
        this.criteriaJson = eventFilter.getCriteriaJson();
        this.userId = eventFilter.getUserId();
        this.createdAt = eventFilter.getCreatedAt();
        this.updatedAt = eventFilter.getUpdatedAt();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getFilterName() {
        return filterName;
    }

    public String getCriteriaJson() {
        return criteriaJson;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

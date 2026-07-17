package com.empujecomunitario.rest_server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EventFilterRequest {

    @NotBlank(message = "El nombre del filtro es obligatorio")
    private String filterName;

    @NotNull(message = "Los criterios no pueden ser nulos")
    private String criteriaJson;

    @NotNull(message = "El ID de usuario es obligatorio")
    private Long userId;

    // Getters y Setters
    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String getCriteriaJson() {
        return criteriaJson;
    }

    public void setCriteriaJson(String criteriaJson) {
        this.criteriaJson = criteriaJson;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
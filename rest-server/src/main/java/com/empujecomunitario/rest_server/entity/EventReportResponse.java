package com.empujecomunitario.rest_server.entity;

import jakarta.persistence.Column;

public class EventReportResponse {
    private Long id;
    private String name;
    private String description;
    private String date;

    @Column(name = "is_completed")
    private boolean isCompleted = false;

    @Column(name = "has_donation_distribution")
    private boolean hasDonationDistribution = false;

    public EventReportResponse(Long id, String name, String description, String date,
            Boolean hasDonationDistribution, Boolean isCompleted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.hasDonationDistribution = hasDonationDistribution;
        this.isCompleted = isCompleted;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public Boolean getHasDonationDistribution() {
        return hasDonationDistribution;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

}

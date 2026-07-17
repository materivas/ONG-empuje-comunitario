package com.grupo_c.SistemasDistribuidosTP.entity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "event_inventory")
public class EventInventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "idEvent", nullable = false)
    private Event event;
    
    @ManyToOne
    @JoinColumn(name = "idInventory", nullable = false)
    private Inventory inventory;

    @ManyToOne
    @JoinColumn(name = "idUser", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private Integer quantityDistributed;

    @Column(nullable = false)
    private LocalDateTime distributionDate;
    
    // Constructores
    public EventInventory() {
        this.distributionDate = LocalDateTime.now(ZoneOffset.ofHours(-3));
    }
    
    public EventInventory(Event event, Inventory inventory, User user ,Integer quantityDistributed) {
        this();
        this.event = event;
        this.inventory = inventory;
        this.user = user;
        this.quantityDistributed = quantityDistributed;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
    
    public Inventory getInventory() { return inventory; }
    public void setInventory(Inventory inventory) { this.inventory = inventory; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Integer getQuantityDistributed() { return quantityDistributed; }
    public void setQuantityDistributed(Integer quantityDistributed) { 
        this.quantityDistributed = quantityDistributed; 
    }
    
    public LocalDateTime getDistributionDate() { return distributionDate; }
    public void setDistributionDate(LocalDateTime distributionDate) { 
        this.distributionDate = distributionDate; 
    }
}
package com.grupo_c.SistemasDistribuidosTP.entity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "events")
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime date;
    
    @Column(nullable = false)
    private Boolean isCompleted = false;
    
    // RELACIÓN: ManyToMany con User
    @ManyToMany
    @JoinTable(
        name = "event_user",
        joinColumns = @JoinColumn(name = "idEvent"),
        inverseJoinColumns = @JoinColumn(name = "idUser")
    )
    private Set<User> participants = new HashSet<>();
    
    // Relación con EventInventory (donaciones repartidas)
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventInventory> distributedInventories = new HashSet<>();
    
    // Constructores
    public Event() {}
    
    public Event(String name, String description, LocalDateTime date) {
        this.name = name;
        this.description = description;
        this.date = date;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { 
        if (date.isBefore(LocalDateTime.now(ZoneOffset.ofHours(-3)))) {
            throw new IllegalArgumentException("La fecha del evento no puede ser en el pasado");
        }
        this.date = date; 
    }
    
    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean isCompleted) { this.isCompleted = isCompleted; }
    
    public Set<User> getParticipants() { return participants; }
    public void setParticipants(Set<User> participants) { this.participants = participants; }
    
    public Set<EventInventory> getDistributedInventories() { return distributedInventories; }
    public void setDistributedInventories(Set<EventInventory> distributedInventories) { 
        this.distributedInventories = distributedInventories; 
    }
    
    // Utils
    public void addParticipant(User user) {
        this.participants.add(user);
        user.getEvents().add(this);
    }
    
    public void removeParticipant(User user) {
        this.participants.remove(user);
        user.getEvents().remove(this);
    }


    public void addDistributedInventory(Inventory inventory, User user, Integer quantity) {
        EventInventory eventInventory = new EventInventory(this, inventory, user, quantity);
        this.distributedInventories.add(eventInventory);
    }

}
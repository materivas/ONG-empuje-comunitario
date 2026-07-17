package com.grupo_c.SistemasDistribuidosTP.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity	
@Table(name = "users")
public class User {
	   	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, length = 100)
    private String surname;
    
    @Column(length = 20)
    private String phoneNumber;
    
    @Column(nullable = false, length = 255)
    private String password;
    
    @Column(unique = true, nullable = false, length = 150)
    private String email;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    
    // Relación con roles
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "idUser"),
        inverseJoinColumns = @JoinColumn(name = "idRole")
    )
    private Set<Role> roles = new HashSet<>();
    
    // Inventarios creados
    @OneToMany(mappedBy = "userCreator")
    private Set<Inventory> createdInventories = new HashSet<>();
    
    // Inventarios modificados
    @OneToMany(mappedBy = "userModify")
    private Set<Inventory> modifiedInventories = new HashSet<>();
    
    // Eventos en los que participa
    @ManyToMany(mappedBy = "participants")
    private Set<Event> events = new HashSet<>();
    
    // Constructores
    public User() {
        this.creationDate = LocalDateTime.now();
        this.modificationDate = LocalDateTime.now();
    }

    public User(
            Set<Role> roles,
            String username,
            String name,
            String surname,
            String phoneNumber,
            String password,
            String email,
            boolean isActive
    ) {
        this.roles = roles;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.email = email;
        this.isActive = isActive;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }
    
    public LocalDateTime getModificationDate() { return modificationDate; }
    public void setModificationDate(LocalDateTime modificationDate) { this.modificationDate = modificationDate; }
    
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public Set<Event> getEvents(){ return events; }
    public void setEvents(Set<Event> events) { this.events = events; }

    // Utils
    public void addRole(Role role) {
        this.roles.add(role);
        role.getUsers().add(this);
    }
    
    public void removeRole(Role role) {
        this.roles.remove(role);
        role.getUsers().remove(this);
    }

    public Set<String> getRolesAsStrings() {
        Set<String> rolesAsStrings = new HashSet<>();
        for(Role role : roles)
            rolesAsStrings.add(role.getName());
        return rolesAsStrings;
    }

    public String getRolesSeparatedByCommaAsString() {
        String rolesSeparatedByComma = "";
        int rolesSize = roles.size();
        List<Role> rolesAsList = roles.stream().toList();
        for(int i = 0 ; i < rolesSize ; i++) {
            String role = rolesAsList.get(i).getName();
            rolesSeparatedByComma = (i < rolesSize - 1) ? rolesSeparatedByComma.concat(role + ",") : rolesSeparatedByComma.concat(role);
        }
        return rolesSeparatedByComma;
    }

    @PreUpdate
    public void preUpdate() {
        this.modificationDate = LocalDateTime.now();
    }
}
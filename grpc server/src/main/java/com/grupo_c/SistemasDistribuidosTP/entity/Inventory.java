package com.grupo_c.SistemasDistribuidosTP.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventories")
public class Inventory {
    
    public enum Category {
        ROPA, ALIMENTOS, JUGUETES, UTILES_ESCOLARES
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Relación con User
    @ManyToOne
    @JoinColumn(name = "idUserCreator", nullable = false)
    private User userCreator;
    
    @ManyToOne
    @JoinColumn(name = "idUserModify", nullable = false)
    private User userModify;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Category category;
    
    @Column(length = 200)
    private String description;
    
    @Column(nullable = false)
    private Integer quantity = 0;
    
    @Column(nullable = false)
    private Boolean isDeleted = false;
    
    @Column(nullable = false)
    private LocalDateTime creationDate;
    
    @Column(nullable = false)
    private LocalDateTime modificationDate;
    
    // Constructores
    public Inventory() {
        this.creationDate = LocalDateTime.now();
        this.modificationDate = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    
    public User getUserCreator() { return userCreator; }
    public void setUserCreator(User userCreator) { this.userCreator = userCreator; }
    
    public User getUserModify() { return userModify; }
    public void setUserModify(User userModify) { this.userModify = userModify; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { 
        if (quantity < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        this.quantity = quantity; 
    }
    
    public Boolean getisDeleted() { return isDeleted; }
    public void setisDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
    
    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }
    
    public LocalDateTime getModificationDate() { return modificationDate; }
    public void setModificationDate(LocalDateTime modificationDate) { this.modificationDate = modificationDate; }
    
    @PreUpdate
    public void preUpdate() {
        this.modificationDate = LocalDateTime.now();
    }
}
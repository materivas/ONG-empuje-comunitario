package com.grupo_c.SistemasDistribuidosTP.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "donations")
@AllArgsConstructor
@Getter @Setter
@ToString
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Inventory.Category category;

    @Column(length = 200)
    private String description;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Column(nullable = false)
    private LocalDate lastDonationDate;

    @Column(nullable = false)
    private Boolean madeByOurselves;

    public Donation() {
        this.quantity = 0;
    }
}

package com.grupo_c.SistemasDistribuidosTP.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class DonationFilter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String category;

    private LocalDate dateFrom;

    private LocalDate dateTo;

    private Boolean deleted;

    @ManyToOne
    private User user;
}

package com.grupo_c.SistemasDistribuidosTP.repository;

import com.grupo_c.SistemasDistribuidosTP.entity.Donation;
import com.grupo_c.SistemasDistribuidosTP.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDonationRepository extends JpaRepository<Donation, Long> {
    boolean existsByCategoryAndDescription(Inventory.Category category, String description);
    Donation findByCategoryAndDescription(Inventory.Category category, String description);
    boolean existsByCategoryAndDescriptionAndMadeByOurselves(Inventory.Category category, String description, Boolean madeByOurselves);
    Donation findByCategoryAndDescriptionAndMadeByOurselves(Inventory.Category category, String description, Boolean madeByOurselves);
}

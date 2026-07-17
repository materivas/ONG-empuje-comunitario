package com.grupo_c.SistemasDistribuidosTP.service;

import com.grupo_c.SistemasDistribuidosTP.entity.Donation;
import com.grupo_c.SistemasDistribuidosTP.entity.Inventory;
import com.grupo_c.SistemasDistribuidosTP.exception.donation.DonationException;

import java.util.List;

public interface IDonationService {
    List<Donation> findAll();
    Donation save(Donation donation) throws DonationException;
    boolean existsByCategoryAndDescriptionAndMadeByOurselves(Inventory.Category category, String description, Boolean madeByOurselves);
    void updateStock(List<Donation> donations) throws DonationException;
    boolean existsByCategoryAndDescription(Inventory.Category category, String description);
    Donation findByCategoryAndDescription(Inventory.Category category, String description);
}

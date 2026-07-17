package com.grupo_c.SistemasDistribuidosTP.serviceImp;

import com.grupo_c.SistemasDistribuidosTP.entity.Donation;
import com.grupo_c.SistemasDistribuidosTP.entity.Inventory;
import com.grupo_c.SistemasDistribuidosTP.exception.donation.DonationException;
import com.grupo_c.SistemasDistribuidosTP.repository.IDonationRepository;
import com.grupo_c.SistemasDistribuidosTP.service.IDonationService;
import com.grupo_c.SistemasDistribuidosTP.validator.DonationValidator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DonationServiceImpl implements IDonationService {
    private final IDonationRepository donationRepository;
    public DonationServiceImpl(IDonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    @Override
    public List<Donation> findAll() {
        return donationRepository.findAll();
    }

    @Transactional
    @Override
    public Donation save(Donation donation) throws DonationException {
        DonationValidator.validate(donation);
        Inventory.Category category = donation.getCategory();
        String description = donation.getDescription();
        Boolean madeByOurselves = donation.getMadeByOurselves();
        Donation donationForDB = new Donation();

        //en caso de que ya exista una donacion de este tipo, sobreescribo el objeto con el de la bd
        if(existsByCategoryAndDescriptionAndMadeByOurselves(category, description, madeByOurselves))
            donationForDB = donationRepository.findByCategoryAndDescriptionAndMadeByOurselves(category, description, madeByOurselves);

        Integer newQuantity = donationForDB.getQuantity() + donation.getQuantity();
        donationForDB.setQuantity(newQuantity);
        donationForDB.setCategory(category);
        donationForDB.setDescription(description);
        donationForDB.setIsDeleted(false);
        donationForDB.setLastDonationDate(LocalDate.now());
        donationForDB.setMadeByOurselves(donation.getMadeByOurselves());

        return donationRepository.save(donationForDB);
    }

    @Override
    public boolean existsByCategoryAndDescriptionAndMadeByOurselves(Inventory.Category category, String description, Boolean madeByOurselves) {
        return donationRepository.existsByCategoryAndDescriptionAndMadeByOurselves(category, description, madeByOurselves);
    }

    @Transactional
    @Override
    public void updateStock(List<Donation> donations) throws DonationException {
        for(Donation donation : donations)
            save(donation);
    }

    @Override
    public boolean existsByCategoryAndDescription(Inventory.Category category, String description) {
        return donationRepository.existsByCategoryAndDescription(category, description);
    }

    @Override
    public Donation findByCategoryAndDescription(Inventory.Category category, String description) {
        return donationRepository.findByCategoryAndDescription(category, description);
    }
}

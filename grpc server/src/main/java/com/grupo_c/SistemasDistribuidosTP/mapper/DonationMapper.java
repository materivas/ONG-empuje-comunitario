package com.grupo_c.SistemasDistribuidosTP.mapper;

import com.grupo_c.SistemasDistribuidosTP.entity.Donation;
import com.grupo_c.SistemasDistribuidosTP.entity.Inventory;
import com.grupo_c.SistemasDistribuidosTP.service.DonationServiceClass;

import java.util.ArrayList;
import java.util.List;

public class DonationMapper {
    public static DonationServiceClass.DonationDTO toDonationDTO(Donation donation) {
        return DonationServiceClass.DonationDTO.newBuilder()
                .setCategory(donation.getCategory().name())
                .setDescription(donation.getDescription())
                .setQuantity(donation.getQuantity())
                .setIsDeleted(donation.getIsDeleted())
                .setMadeByOurselves(donation.getMadeByOurselves())
                .build();
    }

    public static DonationServiceClass.DonationListResponse toDonationListResponse(List<Donation> donations) {
        DonationServiceClass.DonationListResponse.Builder builder = DonationServiceClass.DonationListResponse.newBuilder();
        for(Donation donation : donations)
            builder.addDonations(toDonationDTO(donation));
        return builder.build();
    }

    public static Donation toDonation(DonationServiceClass.DonationDTO request) {
        Donation donation = new Donation();
        //esto esta hardcodeado, podria ir en una funcion aparte en una clase de utileria pero por ahora lo dejo aca
        switch(request.getCategory()) {
            case "ROPA" : donation.setCategory(Inventory.Category.ROPA); break;
            case "ALIMENTOS" : donation.setCategory(Inventory.Category.ALIMENTOS); break;
            case "JUGUETES" : donation.setCategory(Inventory.Category.JUGUETES); break;
            case "UTILES_ESCOLARES" : donation.setCategory(Inventory.Category.UTILES_ESCOLARES); break;
        }
        donation.setDescription(request.getDescription());
        donation.setQuantity(request.getQuantity());
        donation.setIsDeleted(request.getIsDeleted());
        donation.setMadeByOurselves(request.getMadeByOurselves());
        return donation;
    }

    public static Donation toDonation(DonationServiceClass.DonationItem item) {
        Donation donation = new Donation();
        //esto esta hardcodeado, podria ir en una funcion aparte en una clase de utileria pero por ahora lo dejo aca
        switch(item.getCategory()) {
            case "ROPA" : donation.setCategory(Inventory.Category.ROPA); break;
            case "ALIMENTOS" : donation.setCategory(Inventory.Category.ALIMENTOS); break;
            case "JUGUETES" : donation.setCategory(Inventory.Category.JUGUETES); break;
            case "UTILES_ESCOLARES" : donation.setCategory(Inventory.Category.UTILES_ESCOLARES); break;
        }
        donation.setDescription(item.getDescription());
        donation.setQuantity(item.getQuantity());
        donation.setIsDeleted(false);
        return donation;
    }

    public static List<Donation> toDonations(List<DonationServiceClass.DonationItem> itemsList) {
        List<Donation> donations = new ArrayList<>();
        for(DonationServiceClass.DonationItem item : itemsList)
            donations.add(toDonation(item));
        return donations;
    }
}

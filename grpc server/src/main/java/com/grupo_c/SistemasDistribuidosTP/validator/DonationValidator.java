package com.grupo_c.SistemasDistribuidosTP.validator;

import com.grupo_c.SistemasDistribuidosTP.entity.Donation;
import com.grupo_c.SistemasDistribuidosTP.entity.Inventory;
import com.grupo_c.SistemasDistribuidosTP.exception.donation.DonationException;

public class DonationValidator {
    public static void validate(Donation donation) throws DonationException {
        if(thisCategoryIsNotValid(donation.getCategory()))
            throw new DonationException("La categoría de la donación no puede quedar vacía.");
        if(thisDescriptionIsNotValid(donation.getDescription()))
            throw new DonationException("La descripción de la donación no puede quedar vacía.");
        if(thisQuantityIsNotValid(donation.getQuantity()))
            throw new DonationException("La cantidad donada no puede ser menor o igual a cero.");
    }
    public static boolean thisCategoryIsNotValid(Inventory.Category category) {
        if(category == null)
            return true;
        String categoryName = category.name();
        return categoryName.isBlank();
    }
    public static boolean thisDescriptionIsNotValid(String description) {
        return description.isBlank();
    }
    public static boolean thisQuantityIsNotValid(Integer quantity) {
        if(quantity == null)
            return true;
        return quantity <= 0;
    }
}

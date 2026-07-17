package com.empujecomunitario.rest_server.serviceImpl;

import com.empujecomunitario.rest_server.entity.Donation;
import com.empujecomunitario.rest_server.repository.IDonationRepository;
import com.empujecomunitario.rest_server.service.IDonationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonationService implements IDonationService {
    private final IDonationRepository donationRepository;

    public DonationService(IDonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    @Override
    public List<Donation> findAll() {
        return donationRepository.findAll();
    }
}
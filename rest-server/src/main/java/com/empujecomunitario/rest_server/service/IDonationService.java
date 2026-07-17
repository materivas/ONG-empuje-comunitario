package com.empujecomunitario.rest_server.service;

import com.empujecomunitario.rest_server.entity.Donation;

import java.util.List;

public interface IDonationService {
    List<Donation> findAll();
}
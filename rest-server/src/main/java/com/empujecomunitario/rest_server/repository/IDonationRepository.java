package com.empujecomunitario.rest_server.repository;

import com.empujecomunitario.rest_server.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDonationRepository extends JpaRepository<Donation, Long> {
}
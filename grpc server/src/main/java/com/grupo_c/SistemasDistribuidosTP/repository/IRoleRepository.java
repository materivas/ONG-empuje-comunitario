package com.grupo_c.SistemasDistribuidosTP.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grupo_c.SistemasDistribuidosTP.entity.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByName(String name);
    
    List<Role> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT r FROM Role r WHERE r.name IN :roleNames")
    List<Role> findByNames(@Param("roleNames") List<String> roleNames);
    
    boolean existsByName(String name);
    
    @Query("SELECT r FROM Role r ORDER BY r.name ASC")
    List<Role> findAllOrderedByName();
}
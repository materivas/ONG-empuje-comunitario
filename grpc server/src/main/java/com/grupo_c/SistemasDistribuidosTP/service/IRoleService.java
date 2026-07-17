package com.grupo_c.SistemasDistribuidosTP.service;

import com.grupo_c.SistemasDistribuidosTP.entity.Role;

import java.util.List;
import java.util.Set;

public interface IRoleService {
    Role findByName(String name);
    List<Role> findByNameContainingIgnoreCase(String name);
    List<Role> findByNames(List<String> roleNames);
    boolean existsByName(String name);
    List<Role> findAllOrderedByName();
    List<Role> findAll();
    void saveAll(Set<Role> roles);
}

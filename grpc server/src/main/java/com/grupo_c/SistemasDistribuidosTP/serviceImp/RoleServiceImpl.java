package com.grupo_c.SistemasDistribuidosTP.serviceImp;

import com.grupo_c.SistemasDistribuidosTP.entity.Role;
import com.grupo_c.SistemasDistribuidosTP.repository.IRoleRepository;
import com.grupo_c.SistemasDistribuidosTP.service.IRoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class RoleServiceImpl implements IRoleService {
    private final IRoleRepository roleRepository;
    public RoleServiceImpl(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name).orElse(null);
    }

    @Override
    public List<Role> findByNameContainingIgnoreCase(String name) {
        return roleRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Role> findByNames(List<String> roleNames) {
        return roleRepository.findByNames(roleNames);
    }

    @Override
    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }

    @Override
    public List<Role> findAllOrderedByName() {
        return roleRepository.findAllOrderedByName();
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public void saveAll(Set<Role> roles) {
        roleRepository.saveAll(roles);
    }
}

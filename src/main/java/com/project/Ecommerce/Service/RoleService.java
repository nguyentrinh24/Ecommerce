package com.project.Ecommerce.Service;


import com.project.Ecommerce.Model.Role;
import com.project.Ecommerce.Repository.RoleRepository;
import com.project.Ecommerce.Service.Iml.RoleServiceIml;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class RoleService implements RoleServiceIml {
    private final RoleRepository roleRepository;


    @Override
    @Transactional
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}

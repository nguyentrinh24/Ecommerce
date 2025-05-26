package com.project.Ecommerce.Controller;



import com.project.Ecommerce.Model.Role;
import com.project.Ecommerce.Service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("${api.prefix}/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping("")
    public List<Role> getRoles() {
        return roleService.getAllRoles();
    }
}

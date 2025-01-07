package com.narinder.userservice.controllers;

import com.narinder.userservice.services.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RolesController {

    private final RoleService roleService;

    public RolesController(RoleService roleService)  {
        this.roleService = roleService;
    }

    @PutMapping(path = "/assign/users/{userId}")
    public ResponseEntity<?> addRole(@PathVariable String userId, @RequestParam String roleName) {
        roleService.createRole(userId, roleName);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}

package com.narinder.userservice.services.impl;

import com.narinder.userservice.exceptions.RoleAdditionException;
import com.narinder.userservice.services.RoleService;
import com.narinder.userservice.services.UserService;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;


@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    private static final String ROLE_TENANT = "Tenant";

    private static final String ROLE_LANDLORD = "Landlord";

    private final UserService userService;

    @Value("${app.keycloak.realm}")
    private String realm;

    private final Keycloak keycloak;

    public RoleServiceImpl(UserService userService, Keycloak keycloak) {
        this.userService = userService;
        this.keycloak = keycloak;
    }

    @Override
    public void createRole(String userId, String roleName) {
        RolesResource rolesResource = getRolesResource();
        RoleRepresentation representation = rolesResource.get(roleName).toRepresentation();

        if(checkCurrentRoles(userId)) {
            userService.getUser(userId).roles().realmLevel().add(Collections.singletonList(representation));
            logger.info("Role {} added to user {}", roleName, userId);
            return;
        }

        logger.info("Role adding operation failed");
        throw new RoleAdditionException("This role already exists or another role is already present!!!");
    }

    /**
     *
     * @param userId
     * @return true if there is no role present currently on the user profile
     */
    private boolean checkCurrentRoles(String userId) {
        RoleRepresentation roleRepresentation = userService.getUser(userId).roles().realmLevel()
                .listAll()
                .stream()
                .filter(n -> Objects.equals(n.getName(), ROLE_LANDLORD) || Objects.equals(n.getName(), ROLE_TENANT))
                .findAny()
                .orElse(null);
        return roleRepresentation == null;
    }

    private RolesResource getRolesResource() {
        return keycloak.realm(realm).roles();
    }
}

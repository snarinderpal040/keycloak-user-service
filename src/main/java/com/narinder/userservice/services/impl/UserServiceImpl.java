package com.narinder.userservice.services.impl;

import com.narinder.userservice.exceptions.UserCreationFailed;
import com.narinder.userservice.exceptions.UserDeletionException;
import com.narinder.userservice.models.UserCreationRequest;
import com.narinder.userservice.services.UserService;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Value("${app.keycloak.realm}")
    private String realm;

    private final Keycloak keycloak;

    public UserServiceImpl(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    @Override
    public void createUser(UserCreationRequest userCreationRequest) {
        UserRepresentation userRepresentation = getUserRepresentation(userCreationRequest);

        UsersResource usersResource = getUsersResource();
        try (Response response = usersResource.create(userRepresentation)) {
            if (Objects.equals(201, response.getStatus())) {
                logger.info("User created successfully");
                return;
            }
            if(Objects.equals(409, response.getStatus())) {
                throw new UserCreationFailed("User already exists");
            }

            /* This is what we actually suppose to do. After creating an account we should email the customer to verify
            the email address provided.

            List<UserRepresentation> userRepresentations = usersResource.searchByUsername(userRecord.username(), true);
            UserRepresentation userRepresentation1 = userRepresentations.get(0);
            sendVerificationEmail(userRepresentation1.getId());

             */

            throw new UserCreationFailed("User creation failed with status " + response.getStatus());
        }
    }

    @Override
    public void sendVerificationEmail(String userId) {
        UsersResource usersResource = getUsersResource();
        usersResource.get(userId).sendVerifyEmail();
    }

    @Override
    public void deleteUser(String userId) {
        UsersResource usersResource = getUsersResource();
        try(Response response = usersResource.delete(userId)){
            if(!Objects.equals(204, response.getStatus())) {
                throw new UserDeletionException("Error while deleting the user");
            }
            logger.info("User deleted successfully");
        }

    }

    @Override
    public void forgotPassword(String email) {
        UsersResource usersResource = getUsersResource();
        List<UserRepresentation> userRepresentations = usersResource.searchByEmail(email, true);
        UserRepresentation userFound = userRepresentations.get(0);

        UserResource userResourceToExecuteActionsOn = usersResource.get(userFound.getId());
        userResourceToExecuteActionsOn.executeActionsEmail(List.of("UPDATE_PASSWORD"));
    }

    @Override
    public UserResource getUser(String userId) {
        return getUsersResource().get(userId);
    }

    private static UserRepresentation getUserRepresentation(UserCreationRequest userCreationRequest) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setFirstName(userRepresentation.getFirstName());
        userRepresentation.setLastName(userCreationRequest.getLastName());
        userRepresentation.setEmail(userCreationRequest.getEmail());
        userRepresentation.setUsername(userRepresentation.getUsername());
        userRepresentation.setEmailVerified(true);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(userCreationRequest.getPassword());

        userRepresentation.setCredentials(List.of(credentialRepresentation));
        return userRepresentation;
    }

    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }

}

package com.narinder.userservice.services;

import com.narinder.userservice.models.UserCreationRequest;
import org.keycloak.admin.client.resource.UserResource;

public interface UserService {

    void createUser(UserCreationRequest userCreationRequest);

    void sendVerificationEmail(String userId);

    void deleteUser(String userId);

    void forgotPassword(String email);

    UserResource getUser(String userId);

}

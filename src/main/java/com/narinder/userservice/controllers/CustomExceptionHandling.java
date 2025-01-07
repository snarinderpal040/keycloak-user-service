package com.narinder.userservice.controllers;

import com.narinder.userservice.exceptions.RoleAdditionException;
import com.narinder.userservice.exceptions.UserCreationFailed;
import com.narinder.userservice.exceptions.UserDeletionException;
import com.narinder.userservice.models.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class CustomExceptionHandling {

    @ExceptionHandler({UserCreationFailed.class, UserDeletionException.class, RoleAdditionException.class})
    public ResponseEntity<ErrorResponse> handleUserCreationFailed(HttpServletRequest request, Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}

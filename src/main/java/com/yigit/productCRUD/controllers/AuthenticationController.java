package com.yigit.productCRUD.controllers;

import com.yigit.productCRUD.Service.UserService;
import com.yigit.productCRUD.models.ProjectConstants;
import com.yigit.productCRUD.models.*;
import com.yigit.productCRUD.requests.login.LoginRequest;
import com.yigit.productCRUD.requests.registration.RegistrationRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Authentication controller.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("")
public class AuthenticationController {

    /**
     * The User service.
     */
    @Autowired
    UserService userService;

    /**
     * The Rabbit template.
     */
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * Authenticate user response entity.
     *
     * @param loginRequest the login request
     * @return the response entity
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            LoginStatus loginStatus = new LoginStatus(loginRequest,"LOGIN","Login successful");
            rabbitTemplate.convertAndSend(ProjectConstants.EXCHANGE,ProjectConstants.ROUTING_KEY,loginStatus);
            return userService.authenticateUser(loginRequest);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Register user response entity.
     *
     * @param registrationRequest the registration request
     * @return the response entity
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest registrationRequest){
        try {
            RegistrationStatus registrationStatus = new RegistrationStatus(registrationRequest,"REGISTRATION","Registration successful.");
            rabbitTemplate.convertAndSend(ProjectConstants.EXCHANGE,ProjectConstants.ROUTING_KEY,registrationStatus);
            return userService.registerUser(registrationRequest);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

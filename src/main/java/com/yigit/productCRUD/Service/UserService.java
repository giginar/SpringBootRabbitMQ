package com.yigit.productCRUD.Service;

import com.yigit.productCRUD.models.User;
import com.yigit.productCRUD.models.UserRole;
import com.yigit.productCRUD.repository.UserRepository;
import com.yigit.productCRUD.requests.login.LoginRequest;
import com.yigit.productCRUD.requests.registration.RegistrationRequest;
import com.yigit.productCRUD.responses.JwtResponse;
import com.yigit.productCRUD.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The type User service.
 */
@Service
public class UserService implements UserDetailsService {
    /**
     * The Authentication manager.
     */
    @Autowired
    AuthenticationManager authenticationManager;

    /**
     * The User repository.
     */
    @Autowired
    UserRepository userRepository;

    /**
     * The Password encoder.
     */
    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * The Jwt utils.
     */
    @Autowired
    JwtUtils jwtUtils;

    /**
     * Authenticate user response entity.
     *
     * @param loginRequest the login request
     * @return the response entity
     * @throws UsernameNotFoundException the username not found exception
     */
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) throws UsernameNotFoundException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User userDetails = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                new JwtResponse(jwt, userDetails.getId(), userDetails.getEmail(), "USER"));
    }


    /**
     * Register user response entity.
     *
     * @param registrationRequest the registration request
     * @return the response entity
     */
    public ResponseEntity<?> registerUser(RegistrationRequest registrationRequest) {
        List<User> users = userRepository.findAll();
        for (User us: users) {
            if (us.getEmail().equals(registrationRequest.getEmail())){
                return ResponseEntity.badRequest().body("Email is in use.");
            }
        }

        User appUser = new User(registrationRequest.getFirstName(), registrationRequest.getLastName(),
                registrationRequest.getEmail(), passwordEncoder.encode(registrationRequest.getPassword()),
                UserRole.USER);

        userRepository.save(appUser);

        return ResponseEntity.ok("User registered successfully!");
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        List<User> users = userRepository.findAll();
        for(User user:users){
            if(user.getEmail().equals(email)){
                return User.build(user);
            }else {
                throw new UsernameNotFoundException("User Not Found with username: " + email);
            }
        }
        throw new UsernameNotFoundException("User Not Found with username: " + email);
    }
}

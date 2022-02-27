package com.yigit.productCRUD.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yigit.productCRUD.models.User;
import com.yigit.productCRUD.models.UserRole;
import com.yigit.productCRUD.repository.UserRepository;
import com.yigit.productCRUD.requests.login.LoginRequest;
import com.yigit.productCRUD.requests.registration.RegistrationRequest;
import com.yigit.productCRUD.responses.JwtResponse;
import com.yigit.productCRUD.security.jwt.JwtUtils;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * The type User service test.
 */
@ContextConfiguration(classes = {UserService.class, JwtUtils.class})
@ExtendWith(SpringExtension.class)
class UserServiceTest {
    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    /**
     * Test authenticate user.
     *
     * @throws AuthenticationException the authentication exception
     */
    @Test
    void testAuthenticateUser() throws AuthenticationException {
        when(this.jwtUtils.generateJwtToken((Authentication) any())).thenThrow(new UsernameNotFoundException("Msg"));
        when(this.authenticationManager.authenticate((Authentication) any()))
                .thenReturn(new TestingAuthenticationToken("Principal", "Credentials"));
        assertThrows(UsernameNotFoundException.class,
                () -> this.userService.authenticateUser(new LoginRequest("yigit.kucukcinar@example.org", "password")));
        verify(this.jwtUtils).generateJwtToken((Authentication) any());
        verify(this.authenticationManager).authenticate((Authentication) any());
    }

    /**
     * Test authenticate user 2.
     *
     * @throws AuthenticationException the authentication exception
     */
    @Test
    void testAuthenticateUser2() throws AuthenticationException {
        when(this.jwtUtils.generateJwtToken((Authentication) any())).thenReturn("ABC123");

        User user = new User();
        user.setLastName("Kucukcinar");
        user.setEmail("yigit.kucukcinar@example.org");
        user.setPassword("password");
        user.setId(123L);
        user.setUserRole(UserRole.USER);
        user.setFirstName("yigit");
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user, "Credentials");

        when(this.authenticationManager.authenticate((Authentication) any())).thenReturn(testingAuthenticationToken);
        ResponseEntity<?> actualAuthenticateUserResult = this.userService
                .authenticateUser(new LoginRequest("yigit.kucukcinar@example.org", "password"));
        assertTrue(actualAuthenticateUserResult.hasBody());
        assertTrue(actualAuthenticateUserResult.getHeaders().isEmpty());
        assertEquals(HttpStatus.OK, actualAuthenticateUserResult.getStatusCode());
        assertEquals("yigit.kucukcinar@example.org", ((JwtResponse) actualAuthenticateUserResult.getBody()).getEmail());
        assertEquals("ABC123", ((JwtResponse) actualAuthenticateUserResult.getBody()).getAccessToken());
        assertEquals("Bearer", ((JwtResponse) actualAuthenticateUserResult.getBody()).getTokenType());
        assertEquals(123L, ((JwtResponse) actualAuthenticateUserResult.getBody()).getId());
        assertEquals("USER", ((JwtResponse) actualAuthenticateUserResult.getBody()).getRole());
        verify(this.jwtUtils).generateJwtToken((Authentication) any());
        verify(this.authenticationManager).authenticate((Authentication) any());
    }

    /**
     * Test authenticate user 3.
     *
     * @throws AuthenticationException the authentication exception
     */
    @Test
    void testAuthenticateUser3() throws AuthenticationException {
        when(this.jwtUtils.generateJwtToken((Authentication) any())).thenReturn("ABC123");
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("yigit.kucukcinar@example.org");
        when(user.getId()).thenReturn(123L);
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user, "Credentials");

        when(this.authenticationManager.authenticate((Authentication) any())).thenReturn(testingAuthenticationToken);
        ResponseEntity<?> actualAuthenticateUserResult = this.userService
                .authenticateUser(new LoginRequest("yigit.kucukcinar@example.org", "password"));
        assertTrue(actualAuthenticateUserResult.hasBody());
        assertTrue(actualAuthenticateUserResult.getHeaders().isEmpty());
        assertEquals(HttpStatus.OK, actualAuthenticateUserResult.getStatusCode());
        assertEquals("yigit.kucukcinar@example.org", ((JwtResponse) actualAuthenticateUserResult.getBody()).getEmail());
        assertEquals("ABC123", ((JwtResponse) actualAuthenticateUserResult.getBody()).getAccessToken());
        assertEquals("Bearer", ((JwtResponse) actualAuthenticateUserResult.getBody()).getTokenType());
        assertEquals(123L, ((JwtResponse) actualAuthenticateUserResult.getBody()).getId());
        assertEquals("USER", ((JwtResponse) actualAuthenticateUserResult.getBody()).getRole());
        verify(this.jwtUtils).generateJwtToken((Authentication) any());
        verify(this.authenticationManager).authenticate((Authentication) any());
        verify(user).getEmail();
        verify(user).getId();
    }

    /**
     * Test register user.
     */
    @Test
    void testRegisterUser() {
        User user = new User();
        user.setLastName("Kucukcinar");
        user.setEmail("yigit.kucukcinar@example.org");
        user.setPassword("password");
        user.setId(123L);
        user.setUserRole(UserRole.USER);
        user.setFirstName("yigit");
        when(this.userRepository.save((User) any())).thenReturn(user);
        when(this.userRepository.findAll()).thenReturn(new ArrayList<User>());
        when(this.passwordEncoder.encode((CharSequence) any())).thenReturn("secret");
        ResponseEntity<?> actualRegisterUserResult = this.userService
                .registerUser(new RegistrationRequest("yigit", "Kucukcinar", "password", "yigit.kucukcinar@example.org"));
        assertEquals("User registered successfully!", actualRegisterUserResult.getBody());
        assertEquals("<200 OK OK,User registered successfully!,[]>", actualRegisterUserResult.toString());
        assertEquals(HttpStatus.OK, actualRegisterUserResult.getStatusCode());
        assertTrue(actualRegisterUserResult.getHeaders().isEmpty());
        verify(this.userRepository).findAll();
        verify(this.userRepository).save((User) any());
        verify(this.passwordEncoder).encode((CharSequence) any());
    }

    /**
     * Test register user 2.
     */
    @Test
    void testRegisterUser2() {
        User user = new User();
        user.setLastName("Kucukcinar");
        user.setEmail("yigit.kucukcinar@example.org");
        user.setPassword("password");
        user.setId(123L);
        user.setUserRole(UserRole.USER);
        user.setFirstName("yigit");

        ArrayList<User> userList = new ArrayList<User>();
        userList.add(user);

        User user1 = new User();
        user1.setLastName("Kucukcinar");
        user1.setEmail("yigit.kucukcinar@example.org");
        user1.setPassword("password");
        user1.setId(123L);
        user1.setUserRole(UserRole.USER);
        user1.setFirstName("yigit");
        when(this.userRepository.save((User) any())).thenReturn(user1);
        when(this.userRepository.findAll()).thenReturn(userList);
        when(this.passwordEncoder.encode((CharSequence) any())).thenReturn("secret");
        ResponseEntity<?> actualRegisterUserResult = this.userService
                .registerUser(new RegistrationRequest("yigit", "Kucukcinar", "password", "yigit.kucukcinar@example.org"));
        assertEquals("Email is in use.", actualRegisterUserResult.getBody());
        assertEquals("<400 BAD_REQUEST Bad Request,Email is in use.,[]>", actualRegisterUserResult.toString());
        assertEquals(HttpStatus.BAD_REQUEST, actualRegisterUserResult.getStatusCode());
        assertTrue(actualRegisterUserResult.getHeaders().isEmpty());
        verify(this.userRepository).findAll();
    }

    /**
     * Test load user by username.
     *
     * @throws UsernameNotFoundException the username not found exception
     */
    @Test
    void testLoadUserByUsername() throws UsernameNotFoundException {
        when(this.userRepository.findAll()).thenReturn(new ArrayList<User>());
        assertThrows(UsernameNotFoundException.class, () -> this.userService.loadUserByUsername("yigit.kucukcinar@example.org"));
        verify(this.userRepository).findAll();
    }

    /**
     * Test load user by username 2.
     *
     * @throws UsernameNotFoundException the username not found exception
     */
    @Test
    void testLoadUserByUsername2() throws UsernameNotFoundException {
        User user = new User();
        user.setLastName("Kucukcinar");
        user.setEmail("yigit.kucukcinar@example.org");
        user.setPassword("password");
        user.setId(123L);
        user.setUserRole(UserRole.USER);
        user.setFirstName("yigit");

        ArrayList<User> userList = new ArrayList<User>();
        userList.add(user);
        when(this.userRepository.findAll()).thenReturn(userList);
        UserDetails actualLoadUserByUsernameResult = this.userService.loadUserByUsername("yigit.kucukcinar@example.org");
        assertEquals("yigit.kucukcinar@example.org", ((User) actualLoadUserByUsernameResult).getEmail());
        assertEquals(UserRole.USER, ((User) actualLoadUserByUsernameResult).getUserRole());
        assertEquals("password", actualLoadUserByUsernameResult.getPassword());
        assertEquals("Kucukcinar", ((User) actualLoadUserByUsernameResult).getLastName());
        assertEquals("yigit", ((User) actualLoadUserByUsernameResult).getFirstName());
        verify(this.userRepository).findAll();
    }

    /**
     * Test load user by username 3.
     *
     * @throws UsernameNotFoundException the username not found exception
     */
    @Test
    void testLoadUserByUsername3() throws UsernameNotFoundException {
        User user = new User();
        user.setLastName("Kucukcinar");
        user.setEmail("yigit.kucukcinar@example.org");
        user.setPassword("password");
        user.setId(123L);
        user.setUserRole(UserRole.USER);
        user.setFirstName("yigit");

        ArrayList<User> userList = new ArrayList<User>();
        userList.add(user);
        when(this.userRepository.findAll()).thenReturn(userList);
        assertThrows(UsernameNotFoundException.class, () -> this.userService.loadUserByUsername("Email"));
        verify(this.userRepository).findAll();
    }
}


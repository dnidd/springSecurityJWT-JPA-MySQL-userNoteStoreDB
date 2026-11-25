package com.snidd.note_store.service;

import com.snidd.note_store.entity.User;
import com.snidd.note_store.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * User storage logic.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Constructor.
     * @param userRepository = UserRepository
     * @param bCryptPasswordEncoder = hash/encrypt.
     */
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }


    /**
     * Encrypt (hash) password before saving user to DB.
     * @param user = user.
     * @return the saved entity.
     */
    public User register(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }


    /**
     * Authenticate user with username and password.
     * Authentication represents the token for an authentication
     * request or for an authenticated principal once the request
     * has been processed by the
     * AuthenticationManager.authenticate(Authentication) method.
     * @param user user
     * @return JWT token.
     */
    public String verify(User user) {
        // Authenticate user with username & password.
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword())
        );
        // if user is authenticated -> generate JWT token for user.
        if(authenticate.isAuthenticated()) {
            return jwtService.generateToken(user);
        }
        return "Failure";

    }
}

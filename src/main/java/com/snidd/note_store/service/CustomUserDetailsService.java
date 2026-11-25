package com.snidd.note_store.service;

import com.snidd.note_store.CustomUserDetails;
import com.snidd.note_store.entity.User;
import com.snidd.note_store.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Fetch specific user from DB.
 * Defines how users are fetched from DB.
 * @Component = object can be injected.
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructor
     * @param userRepository = userRepository.
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * Fetch username from DB for user trying to log in.
     * @param username the username identifying the user whose data is required.
     * @return UserDetails of user.
     * @throws UsernameNotFoundException e.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(Objects.isNull(user)) {
            System.out.println("User not available.");
            throw new UsernameNotFoundException("User not found.");
        }
        return new CustomUserDetails(user);
    }

}

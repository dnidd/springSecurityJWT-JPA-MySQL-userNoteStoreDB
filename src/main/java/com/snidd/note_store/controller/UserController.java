package com.snidd.note_store.controller;

import com.snidd.note_store.entity.User;
import com.snidd.note_store.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * User authentication interaction API.
 */
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * Register user in DB. Endpoint open to all/everyone.
     * in userService - encrypt password before DB store.
     * @param user = user.
     * @return the saved entity.
     */
    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.register(user);
    }


    /**
     * Login user & generate JWT token.
     * Endpoint open to all/everyone.
     * @return "Success" or "Failure".
     */
    @PostMapping("/login")
    public String login(@RequestBody User user) {
        return userService.verify(user);
    }



}

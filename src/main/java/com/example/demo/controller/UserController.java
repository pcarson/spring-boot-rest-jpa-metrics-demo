package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.exception.UserExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "UserController", description = "Endpoint for user management")
@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Returns user", description = "Returns user by id.")
    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("userId") String userId) {
        try {
            var user = userService.getUser(userId);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(summary = "Returns all users", description = "Returns user by email address.")
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @Operation(summary = "Returns user", description = "Returns user by email address.")
    @GetMapping(value = "/user")
    public ResponseEntity<?> getUserByEmailAddress(@RequestParam(value = "emailAddress") String emailAddress) {

        try {
            return ResponseEntity.ok(userService.getUserByEmailAddress(emailAddress));
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Create user", description = "Creates a new user.")
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {

        try {
            return ResponseEntity.ok(userService.createUser(userDTO));
        } catch (UserExistsException e) {
            return ResponseEntity.badRequest().body("A user with this email already exists");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Delete user", description = "Delete a user for the specified Id.")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") String userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}

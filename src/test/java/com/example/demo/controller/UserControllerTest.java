package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.exception.UserExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.exception.UserServiceException;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    @Test
    void testUserCreate() throws UserExistsException, UserServiceException {
        // succeed ...
        when(userService.createUser(any())).thenReturn(getDummyUserDTO());
        ResponseEntity<?> response = controller.createUser(getDummyUserDTO());
        verify(userService).createUser(any());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUserCreateUserAlreadyExists() throws UserExistsException, UserServiceException {
        // succeed ...
        when(userService.createUser(any())).thenThrow(new UserExistsException(""));
        ResponseEntity<?> response = controller.createUser(getDummyUserDTO());
        verify(userService).createUser(any());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUserCreateUserDisasterStrikes() throws UserExistsException, UserServiceException {
        // succeed ...
        when(userService.createUser(any())).thenThrow(new NullPointerException(""));
        ResponseEntity<?> response = controller.createUser(getDummyUserDTO());
        verify(userService).createUser(any());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testGetUserByIdHappyDays() throws UserNotFoundException {
        // succeed ...
        when(userService.getUser(any())).thenReturn(getDummyUserDTO());
        ResponseEntity<UserDTO> response = controller.getUser(UUID.randomUUID().toString());
        verify(userService).getUser(any());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetUserByIdNotFound() throws UserNotFoundException {
        // succeed ...
        when(userService.getUser(any())).thenThrow(new UserNotFoundException("UhOh"));
        ResponseEntity<UserDTO> response = controller.getUser(UUID.randomUUID().toString());
        verify(userService).getUser(any());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetUserByEmailHappyDays() throws UserNotFoundException, UserServiceException {
        // succeed ...
        when(userService.getUserByEmailAddress(any())).thenReturn(getDummyUserDTO());
        ResponseEntity<?> response = controller.getUserByEmailAddress("x@y.com");
        verify(userService).getUserByEmailAddress(any());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetUserByEmailNotFound() throws UserNotFoundException, UserServiceException {
        // succeed ...
        when(userService.getUserByEmailAddress(any())).thenThrow(new UserNotFoundException("UhOh"));
        ResponseEntity<?> response = controller.getUserByEmailAddress("x@y.com");
        verify(userService).getUserByEmailAddress(any());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetUserByEmailDisasterStrikes() throws UserNotFoundException, UserServiceException {
        // succeed ...
        when(userService.getUserByEmailAddress(any())).thenThrow(new NullPointerException());
        ResponseEntity<?> response = controller.getUserByEmailAddress("x@y.com");
        verify(userService).getUserByEmailAddress(any());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testGetAllUsersNoneFound() {
        // succeed ...
        when(userService.getUsers()).thenReturn(Collections.emptyList());
        ResponseEntity<?> response = controller.getAllUsers();
        verify(userService).getUsers();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetAllUsersSomeFound() {
        var found = 5;
        UserDTO[] users = new UserDTO[found];
        Arrays.fill(users, getDummyUserDTO());
        when(userService.getUsers()).thenReturn(Arrays.asList(users));
        ResponseEntity<List<UserDTO>> response = controller.getAllUsers();
        verify(userService).getUsers();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(found, response.getBody().size());
    }

    @Test
    void testDeleteUserByIdHappyDays() throws UserNotFoundException {
        // succeed ...
        ResponseEntity<Void> response = controller.deleteUser(UUID.randomUUID().toString());
        verify(userService).deleteUser(any());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteUserByIdNotFound() throws UserNotFoundException {
        // succeed ...
        doThrow(new UserNotFoundException("")).when(userService).deleteUser(any());
        ResponseEntity<Void> response = controller.deleteUser(UUID.randomUUID().toString());
        verify(userService).deleteUser(any());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private UserDTO getDummyUserDTO() {
        var user = new UserDTO();
        user.setId(UUID.randomUUID().toString());
        user.setEmail("x@y.com");
        user.setPassword("ssshhhh");
        user.setCreated(new Date());
        user.setLastModified(new Date());
        return user;
    }
}

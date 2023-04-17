package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.exception.UserExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.exception.UserServiceException;

import java.util.List;

public interface UserService {

    List<UserDTO> getUsers();

    UserDTO getUser(String id) throws UserNotFoundException;

    UserDTO getUserByEmailAddress(String emailAddress) throws UserNotFoundException, UserServiceException;

    UserDTO createUser(UserDTO userDTO) throws UserExistsException, UserServiceException;

    void deleteUser(String id) throws UserNotFoundException;

}

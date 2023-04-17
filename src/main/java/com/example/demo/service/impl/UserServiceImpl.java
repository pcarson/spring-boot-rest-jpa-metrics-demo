package com.example.demo.service.impl;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.UserExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.exception.UserServiceException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.UserMapperImpl;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper = new UserMapperImpl();

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDTO> getUsers() {
        log.info("Getting all users");

        List<User> userList = userRepository.findAll();

        return userMapper.mapToUserDtoList(userList);
    }

    @Override
    public UserDTO getUser(String id) throws UserNotFoundException {
        log.info("Getting user by id {}", id);

        return userRepository.findById(id)
                .map(userMapper::mapToUserDto)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public UserDTO getUserByEmailAddress(final String emailAddress) throws UserNotFoundException, UserServiceException {

        if (StringUtils.isEmpty(emailAddress)) {
            throw new UserServiceException("Email must not be null");
        }

        return ofNullable(userRepository.findByEmail(emailAddress.toLowerCase()))
                .map(userMapper::mapToUserDto)
                .orElseThrow(() -> new UserNotFoundException(emailAddress));
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) throws UserExistsException, UserServiceException {

        if (userDTO.getEmail() == null) {
            throw new UserServiceException("Email must not be null");
        }

        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new UserExistsException(userDTO.getEmail());
        }

        var user = createConfiguredUser(userDTO); // prepare
        userRepository.save(user); // save
        return userMapper.mapToUserDto(user);
    }

    @Override
    public void deleteUser(String id) throws UserNotFoundException {

        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userRepository.delete(user);
    }

    private User createConfiguredUser(UserDTO userDTO) {
        var userToSave = userMapper.mapToUser(userDTO);

        userToSave.setEmail(userDTO.getEmail().toLowerCase());

        // ignore any provider Id
        userToSave.setId(UUID.randomUUID().toString());
        userToSave.setCreated(new Date());
        userToSave.setLastModified(userToSave.getCreated());
        userToSave.setPassword(userDTO.getPassword());
        return userToSave;
    }

}

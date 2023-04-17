package com.example.demo.mapper;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    User mapToUser(UserDTO userDTO);

    UserDTO mapToUserDto(User user);

    List<UserDTO> mapToUserDtoList(List<User> users);

}

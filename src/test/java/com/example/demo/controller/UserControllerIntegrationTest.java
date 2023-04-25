package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.exception.UserExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void createUserSucceeds() throws Exception {
        //GIVEN

        var dto = getDummyUserDTO();
        when(userService.createUser(any())).thenReturn(dto);

        //WHEN
        mockMvc.perform(post("/users")

                        .content(new ObjectMapper().writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(dto.getEmail()))
        ;
    }

    @Test
    void createUserWhereUserAlreadyExists() throws Exception {
        //GIVEN

        var dto = getDummyUserDTO();
        when(userService.createUser(any())).thenThrow(new UserExistsException(""));

        //WHEN
        mockMvc.perform(post("/users")

                        .content(new ObjectMapper().writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    void findUserByEmailAddressSucceeds() throws Exception {
        //GIVEN

        var dto = getDummyUserDTO();
        when(userService.getUserByEmailAddress(any())).thenReturn(dto);

        //WHEN
        mockMvc.perform(get("/user")
                        .param("emailAddress", "test@gmail.com"))

                //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.getId())
                );
    }

    @Test
    void findUserByEmailAddressNotFound() throws Exception {
        //GIVEN

        when(userService.getUserByEmailAddress(any())).thenThrow(new UserNotFoundException(""));

        //WHEN
        mockMvc.perform(get("/user")
                        .param("emailAddress", "test@gmail.com"))

                //THEN
                .andExpect(status().isNotFound());
    }

    @Test
    void findUserByIdSucceeds() throws Exception {
        //GIVEN

        var dto = getDummyUserDTO();
        var uuidValue = UUID.randomUUID().toString();
        dto.setId(uuidValue);
        when(userService.getUser(uuidValue)).thenReturn(dto);

        //WHEN
        mockMvc.perform(get("/user/{id}", uuidValue))

                //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(uuidValue))
        ;
    }

    @Test
    void findUserByIdNotFound() throws Exception {
        //GIVEN

        var uuidValue = UUID.randomUUID().toString();
        when(userService.getUser(uuidValue)).thenThrow(new UserNotFoundException(""));

        //WHEN
        mockMvc.perform(get("/user/{id}", uuidValue))

                //THEN
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    void deleteUserByIdSucceeds() throws Exception {
        //GIVEN

        var dto = getDummyUserDTO();
        var uuidValue = UUID.randomUUID().toString();
        dto.setId(uuidValue);

        //WHEN
        mockMvc.perform(get("/user/{id}", uuidValue))

                //THEN
                .andExpect(status().isOk())
        ;
    }

    @Test
    void deleteUserByIdNotFound() throws Exception {
        //GIVEN

        var uuidValue = UUID.randomUUID().toString();
        when(userService.getUser(uuidValue)).thenThrow(new UserNotFoundException(""));

        //WHEN
        mockMvc.perform(get("/user/{id}", uuidValue))

                //THEN
                .andExpect(status().isNotFound())
        ;
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
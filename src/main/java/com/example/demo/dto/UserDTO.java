package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class UserDTO implements Serializable {

    private String id;
    private Date created;
    private Date lastModified;
    private String email;
    private String password;
}

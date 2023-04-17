package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "userDetail",
        indexes = {
                @Index(name = "email_idx",
                        columnList = "email", unique = true)
        })
public class User implements Serializable {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "created", unique = true, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date created;

    @Column(name = "lastModified", unique = false, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date lastModified;

    @Column(name = "email", unique = false, nullable = false)
    private String email;

    @Column(name = "password", unique = false, nullable = false)
    private String password;
}

package com.snidd.note_store.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User entity.
 * Annotation @Entity tells JPA that this class User
 * represents DB table user with JPA naming convention.
 * Hibernate connect entity to table.
 * OneToMany relationship to notes.
 */
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String password;

    // mappedBy = "user":
    // relation owned by field 'user' in Note (not column).
    // Tells JPA which Java field (user) is owning the relation.
    @OneToMany(mappedBy = "user")
    private List<Note> notes = new ArrayList<>();


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

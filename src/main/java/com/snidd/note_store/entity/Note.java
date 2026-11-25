package com.snidd.note_store.entity;

import jakarta.persistence.*;

/**
 * Note entity.
 * ManyToOne relationship to user.
 */
@Entity
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    // Column 'user_id' holds FK to 'user'-table PK.
    // user_id is the DB representation of the User-Note relation.
    @ManyToOne
    @JoinColumn(name = "user_id")
    // A reference to the User object to which the Note belongs.
    // When a Note is created a user is associated to the Note, setUser(User user).
    // Java representation of the relation.
    private User user;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

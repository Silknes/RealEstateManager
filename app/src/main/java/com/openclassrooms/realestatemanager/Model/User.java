package com.openclassrooms.realestatemanager.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String email;
    private String password;
    private String username;

    public User(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

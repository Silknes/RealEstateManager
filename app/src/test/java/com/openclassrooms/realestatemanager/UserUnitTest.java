package com.openclassrooms.realestatemanager;

import com.openclassrooms.realestatemanager.Model.User;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserUnitTest {
    private User user;

    @Before
    public void setup(){
        user = new User("email@gmail.com", "password", "username");
    }

    @Test
    public void getId(){
        assertEquals(0, user.getId());
    }

    @Test
    public void getEmail(){
        assertEquals("email@gmail.com", user.getEmail());
    }

    @Test
    public void getPassword(){
        assertEquals("password", user.getPassword());
    }

    @Test
    public void getUsername(){
        assertEquals("username", user.getUsername());
    }
}

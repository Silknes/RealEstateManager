package com.openclassrooms.realestatemanager.Repositories;

import android.arch.lifecycle.LiveData;

import com.openclassrooms.realestatemanager.Database.Dao.UserDao;
import com.openclassrooms.realestatemanager.Model.User;

public class UserDataRepository {
    private final UserDao userDao;

    public UserDataRepository(UserDao userDao) {
        this.userDao = userDao;
    }

    public LiveData<User> getUser(long userId) {
        return this.userDao.getUser(userId);
    }

    public LiveData<User> getUserToLogIn(String userEmail, String userPassword){
        return this.userDao.getUserToLogIn(userEmail, userPassword);
    }

    public LiveData<User> isEmailAlreadyTaken(String userEmail){
        return this.userDao.isEmailAlreadyTaken(userEmail);
    }

    public void createUser(User user){
        userDao.createUser(user);
    }
}

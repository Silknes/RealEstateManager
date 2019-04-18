package com.openclassrooms.realestatemanager;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.openclassrooms.realestatemanager.Model.Property;
import com.openclassrooms.realestatemanager.Model.User;
import com.openclassrooms.realestatemanager.Repositories.PropertyDataRepository;
import com.openclassrooms.realestatemanager.Repositories.UserDataRepository;

import java.util.List;
import java.util.concurrent.Executor;

public class PropertyViewModel extends ViewModel {
    private final PropertyDataRepository propertyDataSource;
    private final UserDataRepository userDataSource;
    private final Executor executor;

    @Nullable
    private LiveData<User> currentUser;

    public PropertyViewModel(PropertyDataRepository propertyDataSource, UserDataRepository userDataSource, Executor executor) {
        this.propertyDataSource = propertyDataSource;
        this.userDataSource = userDataSource;
        this.executor = executor;
    }

    public void init(long userId){
        if(this.currentUser != null){
            return;
        }
        currentUser = userDataSource.getUser(userId);
    }

    public void updateCurrentUser(long userId){
        currentUser = userDataSource.getUser(userId);
    }

    public LiveData<User> getCurrentUser(){
        return this.currentUser;
    }

    public LiveData<User> getPropertyAgent(long userId){
        return userDataSource.getUser(userId);
    }

    public void createUser(User user){
        executor.execute(() -> {
            userDataSource.createUser(user);
        });
    }

    public LiveData<User> getUserToLogIn(String userEmail, String userPassword){
        return userDataSource.getUserToLogIn(userEmail, userPassword);
    }

    public LiveData<List<Property>> getProperties(long userId){
        return propertyDataSource.getProperties(userId);
    }

    public LiveData<List<Property>> getAllProperties(){
        return propertyDataSource.getAllProperties();
    }

    public void createProperty(Property property){
        executor.execute(() -> {
            propertyDataSource.createProperty(property);
        });
    }

    public void deleteProperty(long propertyId){
        executor.execute(() -> {
            propertyDataSource.deleteProperty(propertyId);
        });
    }

    public void updateProperty(Property property){
        executor.execute(() -> {
            propertyDataSource.updateProperty(property);
        });
    }
}

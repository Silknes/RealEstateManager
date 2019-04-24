package com.openclassrooms.realestatemanager;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.openclassrooms.realestatemanager.Model.Photo;
import com.openclassrooms.realestatemanager.Model.Property;
import com.openclassrooms.realestatemanager.Model.User;
import com.openclassrooms.realestatemanager.Repositories.PhotoDataRepository;
import com.openclassrooms.realestatemanager.Repositories.PropertyDataRepository;
import com.openclassrooms.realestatemanager.Repositories.UserDataRepository;

import java.util.List;
import java.util.concurrent.Executor;

public class PropertyViewModel extends ViewModel {
    private final PropertyDataRepository propertyDataSource;
    private final UserDataRepository userDataSource;
    private final PhotoDataRepository photoDataSource;
    private final Executor executor;

    @Nullable
    private LiveData<User> currentUser;

    public PropertyViewModel(PropertyDataRepository propertyDataSource, UserDataRepository userDataSource, PhotoDataRepository photoDataSource, Executor executor) {
        this.propertyDataSource = propertyDataSource;
        this.userDataSource = userDataSource;
        this.photoDataSource = photoDataSource;
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

    public LiveData<Property> getProperty(long propertyId){
        return propertyDataSource.getProperty(propertyId);
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

    public void createPhoto(Photo photo){
        executor.execute(() -> {
            photoDataSource.createPhoto(photo);
        });
    }

    public LiveData<List<Photo>> getPhotosProperty(long propertyId){
        return photoDataSource.getPhotosProperty(propertyId);
    }

    public LiveData<List<Photo>> getMainPhotos(){
        return photoDataSource.getMainPhotos();
    }

    public void deletePhoto(long photoId){
        executor.execute(() -> {
            photoDataSource.deletePhoto(photoId);
        });
    }

    public void updatePhoto(Photo photo){
        executor.execute(() -> {
            photoDataSource.updatePhoto(photo);
        });
    }
}

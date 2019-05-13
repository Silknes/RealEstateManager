package com.openclassrooms.realestatemanager.Injections;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.openclassrooms.realestatemanager.View.PropertyViewModel;
import com.openclassrooms.realestatemanager.Repositories.PhotoDataRepository;
import com.openclassrooms.realestatemanager.Repositories.PropertyDataRepository;
import com.openclassrooms.realestatemanager.Repositories.UserDataRepository;

import java.util.concurrent.Executor;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final PropertyDataRepository propertyDataSource;
    private final UserDataRepository userDataSource;
    private final PhotoDataRepository photoDataSource;
    private final Executor executor;

    public ViewModelFactory(PropertyDataRepository propertyDataSource, UserDataRepository userDataSource, PhotoDataRepository photoDataSource, Executor executor) {
        this.propertyDataSource = propertyDataSource;
        this.userDataSource = userDataSource;
        this.photoDataSource = photoDataSource;
        this.executor = executor;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(PropertyViewModel.class)){
            return (T) new PropertyViewModel(propertyDataSource, userDataSource, photoDataSource, executor);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

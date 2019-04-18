package com.openclassrooms.realestatemanager.Injections;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.openclassrooms.realestatemanager.PropertyViewModel;
import com.openclassrooms.realestatemanager.Repositories.PropertyDataRepository;
import com.openclassrooms.realestatemanager.Repositories.UserDataRepository;

import java.util.concurrent.Executor;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final PropertyDataRepository propertyDataSource;
    private final UserDataRepository userDataSource;
    private final Executor executor;

    public ViewModelFactory(PropertyDataRepository propertyDataSource, UserDataRepository userDataSource, Executor executor) {
        this.propertyDataSource = propertyDataSource;
        this.userDataSource = userDataSource;
        this.executor = executor;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(PropertyViewModel.class)){
            return (T) new PropertyViewModel(propertyDataSource, userDataSource, executor);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

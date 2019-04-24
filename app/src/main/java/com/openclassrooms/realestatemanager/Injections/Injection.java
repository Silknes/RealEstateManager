package com.openclassrooms.realestatemanager.Injections;

import android.content.Context;

import com.openclassrooms.realestatemanager.Database.RealEstateManagerDatabase;
import com.openclassrooms.realestatemanager.Repositories.PhotoDataRepository;
import com.openclassrooms.realestatemanager.Repositories.PropertyDataRepository;
import com.openclassrooms.realestatemanager.Repositories.UserDataRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Injection {
    public static PropertyDataRepository providePropertyDataSource(Context context){
        RealEstateManagerDatabase database = RealEstateManagerDatabase.getInstance(context);
        return new PropertyDataRepository(database.propertyDao());
    }

    public static UserDataRepository provideUserDataSource(Context context){
        RealEstateManagerDatabase database = RealEstateManagerDatabase.getInstance(context);
        return new UserDataRepository(database.userDao());
    }

    public static PhotoDataRepository providePhotoDataSource(Context context){
        RealEstateManagerDatabase database = RealEstateManagerDatabase.getInstance(context);
        return new PhotoDataRepository(database.photoDao());
    }

    public static Executor provideExecutor(){
        return Executors.newSingleThreadExecutor();
    }

    public static ViewModelFactory provideViewModelFactory(Context context){
        PropertyDataRepository dataSourceProperty = providePropertyDataSource(context);
        UserDataRepository dataSourceUser = provideUserDataSource(context);
        PhotoDataRepository dataSourcePhoto = providePhotoDataSource(context);
        Executor executor = provideExecutor();
        return new ViewModelFactory(dataSourceProperty, dataSourceUser, dataSourcePhoto, executor);
    }
}

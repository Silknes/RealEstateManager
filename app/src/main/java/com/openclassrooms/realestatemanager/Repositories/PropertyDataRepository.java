package com.openclassrooms.realestatemanager.Repositories;

import android.arch.lifecycle.LiveData;

import com.openclassrooms.realestatemanager.Database.Dao.PropertyDao;
import com.openclassrooms.realestatemanager.Model.Property;

import java.util.List;

public class PropertyDataRepository {
    private final PropertyDao propertyDao;

    public PropertyDataRepository(PropertyDao propertyDao) {
        this.propertyDao = propertyDao;
    }

    public LiveData<List<Property>> getProperties(long userId) {
        return this.propertyDao.getProperties(userId);
    }

    public LiveData<List<Property>> getAllProperties(){
        return this.propertyDao.getAllProperties();
    }

    public void createProperty(Property property){
        propertyDao.insertProperty(property);
    }

    public void deleteProperty(long itemId){
        propertyDao.deleteProperty(itemId);
    }

    public void updateProperty(Property property){
        propertyDao.updateProperty(property);
    }
}

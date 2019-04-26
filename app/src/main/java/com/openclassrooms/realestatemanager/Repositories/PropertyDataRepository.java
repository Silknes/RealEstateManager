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

    public LiveData<Property> getProperty(long propertyId){
        return this.propertyDao.getProperty(propertyId);
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

    /*********************************
    **** Query for searchFragment ****
    *********************************/

    public LiveData<List<Property>> getPropertyBetweenTwoPrice(double minPrice, double maxPrice){
        return this.propertyDao.getAllPropertyBetweenTwoPrice(minPrice, maxPrice);
    }

    public LiveData<List<Property>> getPropertyLessThanPrice(double price){
        return this.propertyDao.getAllPropertyLessThanPrice(price);
    }

    public LiveData<List<Property>> getPropertyMoreThanPrice(double price){
        return this.propertyDao.getAllPropertyMoreThanPrice(price);
    }

    public LiveData<List<Property>> getPropertyBetweenArea(int minArea, int maxArea){
        return this.propertyDao.getAllPropertyBetweenArea(minArea, maxArea);
    }

    public LiveData<List<Property>> getPropertyLessThanArea(int area){
        return this.propertyDao.getAllPropertyLessThanArea(area);
    }

    public LiveData<List<Property>> getPropertyMoreThanArea(int area){
        return this.propertyDao.getAllPropertyMoreThanArea(area);
    }

    public LiveData<List<Property>> getPropertyBetweenNbRoom(int minNbRoom, int maxNbRoom){
        return this.propertyDao.getAllPropertyBetweenNbRoom(minNbRoom, maxNbRoom);
    }

    public LiveData<List<Property>> getPropertyLessThanNbRoom(int minNbRoom){
        return this.propertyDao.getAllPropertyLessThanNbRoom(minNbRoom);
    }

    public LiveData<List<Property>> getPropertyMoreThanNbRoom(int minNbRoom){
        return this.propertyDao.getAllPropertyMoreThanNbRoom(minNbRoom);
    }


    public LiveData<List<Property>> getRequestProperty(double minPrice, double maxPrice,
                                                       int minArea, int maxArea,
                                                       int minNbRoom, int maxNbRoom ,
                                                       boolean schoolState, boolean shopState,
                                                       boolean parcState, boolean transportState,
                                                       int choosenType, int minEntryDate,
                                                       int maxEntryDate, int minSaleDate,
                                                       int maxSaleDate){
        return this.propertyDao.getRequestProperty(minPrice, maxPrice, minArea, maxArea,
                minNbRoom, maxNbRoom, schoolState, shopState, parcState, transportState,
                choosenType, minEntryDate, maxEntryDate, minSaleDate, maxSaleDate);
    }
}

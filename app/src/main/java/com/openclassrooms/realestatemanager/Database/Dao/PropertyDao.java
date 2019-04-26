package com.openclassrooms.realestatemanager.Database.Dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.openclassrooms.realestatemanager.Model.Property;

import java.util.List;

@Dao
public interface PropertyDao {

    @Query("SELECT * FROM Property WHERE userId = :userId")
    LiveData<List<Property>> getProperties(long userId);

    @Query("SELECT * FROM Property")
    LiveData<List<Property>> getAllProperties();

    @Query("SELECT * FROM Property WHERE id = :propertyId")
    LiveData<Property> getProperty(long propertyId);

    @Insert
    long insertProperty(Property property);

    @Update
    int updateProperty(Property property);

    @Query("DELETE FROM Property WHERE id = :propertyId")
    int deleteProperty(long propertyId);

    /*********************************
    **** Query for searchFragment ****
    *********************************/

    @Query("SELECT * FROM Property WHERE price >= :minPrice AND price <= :maxPrice")
    LiveData<List<Property>> getAllPropertyBetweenTwoPrice(double minPrice, double maxPrice);

    @Query("SELECT * FROM Property WHERE price <= :price")
    LiveData<List<Property>> getAllPropertyLessThanPrice(double price);

    @Query("SELECT * FROM Property WHERE price >= :price")
    LiveData<List<Property>> getAllPropertyMoreThanPrice(double price);

    @Query("SELECT * FROM Property WHERE area >= :minArea AND area <= :maxArea")
    LiveData<List<Property>> getAllPropertyBetweenArea(int minArea, int maxArea);

    @Query("SELECT * FROM Property WHERE area <= :area")
    LiveData<List<Property>> getAllPropertyLessThanArea(int area);

    @Query("SELECT * FROM Property WHERE area >= :area")
    LiveData<List<Property>> getAllPropertyMoreThanArea(int area);

    @Query("SELECT * FROM Property WHERE nbRoom >= :minNbRoom AND area <= :maxNbRoom")
    LiveData<List<Property>> getAllPropertyBetweenNbRoom(int minNbRoom, int maxNbRoom);

    @Query("SELECT * FROM Property WHERE nbRoom <= :nbRoom")
    LiveData<List<Property>> getAllPropertyLessThanNbRoom(int nbRoom);

    @Query("SELECT * FROM Property WHERE nbRoom >= :nbRoom")
    LiveData<List<Property>> getAllPropertyMoreThanNbRoom(int nbRoom);



    @Query("SELECT * FROM Property WHERE price >= :minPrice AND price <= :maxPrice " +
            "AND area >= :minArea AND area <= :maxArea " +
            "AND nbRoom >= :minNbRoom AND area <= :maxNbRoom " +
            "AND checkboxSchool = :schoolState AND checkboxShop = :shopState " +
            "AND checkboxParc = :parcState AND checkboxPublicTransport = :transportState " +
            "AND type = :choosenType " +
            "AND entryDate >= :minEntryDate AND entryDate <= :maxEntryDate " +
            "AND saleDate >= :minSaleDate AND saleDate <= :maxSaleDate")
    LiveData<List<Property>> getRequestProperty(double minPrice, double maxPrice,
                                                int minArea, int maxArea,
                                                int minNbRoom, int maxNbRoom,
                                                boolean schoolState, boolean shopState,
                                                boolean parcState, boolean transportState,
                                                int choosenType, int minEntryDate,
                                                int maxEntryDate, int minSaleDate,
                                                int maxSaleDate);
}

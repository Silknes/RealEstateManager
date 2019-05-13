package com.openclassrooms.realestatemanager.Database.Dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

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

    /*************************************
    **** Query used in searchFragment ****
    *************************************/

    @Query("SELECT * FROM Property WHERE price >= :minPrice AND price <= :maxPrice " +
            "AND area >= :minArea AND area <= :maxArea " +
            "AND nbRoom >= :minNbRoom AND area <= :maxNbRoom " +
            "AND type = :choosenType " +
            "AND checkboxSchool = :schoolState AND checkboxShop = :shopState " +
            "AND checkboxParc = :parcState AND checkboxPublicTransport = :transportState " +
            "AND entryDate >= :minEntryDate AND entryDate <= :maxEntryDate " +
            "AND saleDate >= :minSaleDate AND saleDate <= :maxSaleDate " +
            "AND city LIKE :city")
    LiveData<List<Property>> getRequestProperty(double minPrice, double maxPrice,
                                                int minArea, int maxArea,
                                                int minNbRoom, int maxNbRoom,
                                                boolean schoolState, boolean shopState,
                                                boolean parcState, boolean transportState,
                                                int choosenType, int minEntryDate,
                                                int maxEntryDate, int minSaleDate,
                                                int maxSaleDate, String city);

    /**************************************
    **** Query used by ContentProvider ****
    **************************************/

    @Query("SELECT * FROM Property")
    Cursor getPropertiesWithCursor();

    //"AND checkboxSchool = :schoolState AND checkboxShop = :shopState " +
            //"AND checkboxParc = :parcState AND checkboxPublicTransport = :transportState " +
}

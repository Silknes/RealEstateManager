package com.openclassrooms.realestatemanager.Database.Dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.openclassrooms.realestatemanager.Model.Photo;

import java.util.List;


@Dao
public interface PhotoDao {

    @Query("SELECT * FROM Photo WHERE propertyId = :propertyId")
    LiveData<List<Photo>> getPhotosProperty(long propertyId);

    @Query("SELECT * FROM Photo WHERE position = 1")
    LiveData<List<Photo>> getMainPhotos();

    @Insert
    long insertPhoto(Photo photo);

    @Update
    int updatePhoto(Photo photo);

    @Query("DELETE FROM Photo WHERE id = :photoId")
    int deletePhoto(long photoId);
}

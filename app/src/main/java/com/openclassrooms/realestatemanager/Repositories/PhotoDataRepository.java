package com.openclassrooms.realestatemanager.Repositories;

import android.arch.lifecycle.LiveData;

import com.openclassrooms.realestatemanager.Database.Dao.PhotoDao;
import com.openclassrooms.realestatemanager.Model.Photo;

import java.util.List;

public class PhotoDataRepository {
    private final PhotoDao photoDao;

    public PhotoDataRepository(PhotoDao photoDao) {
        this.photoDao = photoDao;
    }

    public LiveData<List<Photo>> getPhotosProperty(long propertyId) {
        return this.photoDao.getPhotosProperty(propertyId);
    }

    public LiveData<List<Photo>> getMainPhotos(){
        return this.photoDao.getMainPhotos();
    }

    public void createPhoto(Photo photo){
        photoDao.insertPhoto(photo);
    }

    public void deletePhoto(long photoId){
        photoDao.deletePhoto(photoId);
    }

    public void updatePhoto(Photo photo){
        photoDao.updatePhoto(photo);
    }
}

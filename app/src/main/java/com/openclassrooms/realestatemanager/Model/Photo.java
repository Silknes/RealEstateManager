package com.openclassrooms.realestatemanager.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.net.Uri;

@Entity(foreignKeys = @ForeignKey(entity = Property.class,
    parentColumns = "id",
    childColumns = "propertyId"))
public class Photo {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private long propertyId;

    private String description;
    private String uriPhoto;
    private int position;

    public Photo(long propertyId, String description, String uriPhoto, int position) {
        this.propertyId = propertyId;
        this.description = description;
        this.uriPhoto = uriPhoto;
        this.position = position;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(long propertyId) {
        this.propertyId = propertyId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUriPhoto() {
        return uriPhoto;
    }

    public void setUriPhoto(String uriPhoto) {
        this.uriPhoto = uriPhoto;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}

package com.openclassrooms.realestatemanager.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(foreignKeys = @ForeignKey(entity = User.class,
    parentColumns = "id",
    childColumns = "userId"))
public class Property implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private long userId;

    private String address, description;
    private double price;
    private int type, status, area, nbRoom, entryDate, saleDate;
    private boolean checkboxSchool, checkboxShop, checkboxParc, checkboxPublicTransport;

    public Property(long userId, int type, String address, String description, int entryDate, double price, int area, int nbRoom, boolean checkboxSchool, boolean checkboxShop, boolean checkboxParc, boolean checkboxPublicTransport) {
        this.userId = userId;
        this.type = type;
        this.address = address;
        this.description = description;
        this.entryDate = entryDate;
        this.price = price;
        this.area = area;
        this.nbRoom = nbRoom;
        this.checkboxSchool = checkboxSchool;
        this.checkboxShop = checkboxShop;
        this.checkboxParc = checkboxParc;
        this.checkboxPublicTransport = checkboxPublicTransport;

        this.status = 2;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(int entryDate) {
        this.entryDate = entryDate;
    }

    public int getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(int saleDate) {
        this.saleDate = saleDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public int getNbRoom() {
        return nbRoom;
    }

    public void setNbRoom(int nbRoom) {
        this.nbRoom = nbRoom;
    }

    public boolean isCheckboxSchool() {
        return checkboxSchool;
    }

    public void setCheckboxSchool(boolean checkboxSchool) {
        this.checkboxSchool = checkboxSchool;
    }

    public boolean isCheckboxShop() {
        return checkboxShop;
    }

    public void setCheckboxShop(boolean checkboxShop) {
        this.checkboxShop = checkboxShop;
    }

    public boolean isCheckboxParc() {
        return checkboxParc;
    }

    public void setCheckboxParc(boolean checkboxParc) {
        this.checkboxParc = checkboxParc;
    }

    public boolean isCheckboxPublicTransport() {
        return checkboxPublicTransport;
    }

    public void setCheckboxPublicTransport(boolean checkboxPublicTransport) {
        this.checkboxPublicTransport = checkboxPublicTransport;
    }
}

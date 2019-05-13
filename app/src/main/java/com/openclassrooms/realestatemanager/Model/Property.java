package com.openclassrooms.realestatemanager.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.content.ContentValues;

import java.io.Serializable;

@Entity(foreignKeys = @ForeignKey(entity = User.class,
    parentColumns = "id",
    childColumns = "userId"))
public class Property implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private long userId;

    private String address, description, city;
    private double price;
    private int type, status, area, nbRoom, entryDate, saleDate, houseNumber, postalCode;
    private boolean checkboxSchool, checkboxShop, checkboxParc, checkboxPublicTransport;

    public Property(){ }

    public Property(long userId, int type, String address, String city, int houseNumber, int postalCode, String description, int entryDate, double price, int area, int nbRoom, boolean checkboxSchool, boolean checkboxShop, boolean checkboxParc, boolean checkboxPublicTransport) {
        this.userId = userId;
        this.type = type;
        this.address = address;
        this.city = city;
        this.houseNumber = houseNumber;
        this.postalCode = postalCode;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
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

    public static Property fromContentValues(ContentValues values){
        final Property property = new Property();
        if(values.containsKey("userId")) property.setUserId(values.getAsLong("userId"));
        if(values.containsKey("type")) property.setType(values.getAsInteger("type"));
        if(values.containsKey("address")) property.setAddress(values.getAsString("address"));
        if(values.containsKey("city")) property.setCity(values.getAsString("city"));
        if(values.containsKey("houseNumber")) property.setHouseNumber(values.getAsInteger("houseNumber"));
        if(values.containsKey("postalCode")) property.setPostalCode(values.getAsInteger("postalCode"));
        if(values.containsKey("description")) property.setDescription(values.getAsString("description"));
        if(values.containsKey("entryDate")) property.setEntryDate(values.getAsInteger("entryDate"));
        if(values.containsKey("price")) property.setPrice(values.getAsDouble("price"));
        if(values.containsKey("area")) property.setArea(values.getAsInteger("area"));
        if(values.containsKey("nbRoom")) property.setNbRoom(values.getAsInteger("nbRoom"));
        if(values.containsKey("status")) property.setStatus(values.getAsInteger("status"));
        if(values.containsKey("saleDate")) property.setSaleDate(values.getAsInteger("saleDate"));
        if(values.containsKey("checkboxSchool")) property.setCheckboxSchool(values.getAsBoolean("checkboxSchool"));
        if(values.containsKey("checkboxShop")) property.setCheckboxShop(values.getAsBoolean("checkboxShop"));
        if(values.containsKey("checkboxParc")) property.setCheckboxParc(values.getAsBoolean("checkboxParc"));
        if(values.containsKey("checkboxPublicTransport")) property.setCheckboxPublicTransport(values.getAsBoolean("checkboxPublicTransport"));

        return property;
    }
}

package com.openclassrooms.realestatemanager;

import com.openclassrooms.realestatemanager.Util.Utils;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class UtilsUnitTest {

    @Test
    public void convertDollarsToEuroTest(){
        int dollarsPrice = 10;
        assertEquals(8, Utils.convertDollarToEuro(dollarsPrice));
    }

    @Test
    public void convertEuroToDollarTest(){
        int euroPrice = 10;
        assertEquals(11, Utils.convertEuroToDollar(euroPrice));
    }

    @Test
    public void getTodayDateTest(){
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        assertEquals(dateFormat.format(date), Utils.getTodayDate());
    }
}

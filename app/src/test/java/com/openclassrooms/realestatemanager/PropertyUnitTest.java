package com.openclassrooms.realestatemanager;

import com.openclassrooms.realestatemanager.Model.Property;
import com.openclassrooms.realestatemanager.Model.User;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PropertyUnitTest {
    private Property property;

    private static final double DELTA = 1e-15;

    @Before
    public void setup(){
        User user = new User("email@gmail.com", "password", "username");

        property = new Property(user.getId(), 1, "boulevard Jean Jaures", "Paris",
                34, 75, "My description", 20190705, 250000,
                128, 8, true, true, false, false);
    }

    @Test
    public void getId(){
        assertEquals(0, property.getId());
    }

    @Test
    public void getUserId(){
        assertEquals(0, property.getUserId());
    }

    @Test
    public void getType(){
        assertEquals(1, property.getType());
    }

    @Test
    public void getAddress(){
        assertEquals("boulevard Jean Jaures", property.getAddress());
    }

    @Test
    public void getCity(){
        assertEquals("Paris", property.getCity());
    }

    @Test
    public void getHouseNb(){
        assertEquals(34, property.getHouseNumber());
    }

    @Test
    public void getPostalCode(){
        assertEquals(75, property.getPostalCode());
    }

    @Test
    public void getDescription(){
        assertEquals("My description", property.getDescription());
    }

    @Test
    public void getEntryDate(){
        assertEquals(20190705, property.getEntryDate());
    }

    @Test
    public void getPrice(){
        assertEquals(250000, property.getPrice(), DELTA);
    }

    @Test
    public void getArea(){
        assertEquals(128, property.getArea());
    }

    @Test
    public void getRoomNb(){
        assertEquals(8, property.getNbRoom());
    }

    @Test
    public void getCheckboxSchool(){
        assertTrue(property.isCheckboxSchool());
    }

    @Test
    public void getCheckboxShop(){
        assertTrue(property.isCheckboxShop());
    }

    @Test
    public void getCheckboxParc(){
        assertFalse(property.isCheckboxParc());
    }

    @Test
    public void getCheckboxTransport(){
        assertFalse(property.isCheckboxPublicTransport());
    }
}

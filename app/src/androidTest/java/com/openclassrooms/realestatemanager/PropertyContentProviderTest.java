package com.openclassrooms.realestatemanager;

import android.arch.persistence.room.Room;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.openclassrooms.realestatemanager.Database.RealEstateManagerDatabase;
import com.openclassrooms.realestatemanager.Provider.PropertyContentProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PropertyContentProviderTest {
    private ContentResolver mContentResolver;

    private static long USER_ID = 1;

    @Before
    public void setUp(){
        Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                RealEstateManagerDatabase.class)
                .allowMainThreadQueries()
                .build();
        mContentResolver = InstrumentationRegistry.getContext().getContentResolver();
    }

    @Test
    public void getItemsWhenNoItemInserted() {
        final Cursor cursor = mContentResolver.query(ContentUris
                .withAppendedId(PropertyContentProvider.URI_PROPERTY, USER_ID),
                null, null, null, null);
        assertThat(cursor, notNullValue());
        assertThat(cursor.getCount(), is(0));
        cursor.close();
    }

    @Test
    public void insertAndGetProperty(){
        final Uri uri = mContentResolver.insert(PropertyContentProvider.URI_PROPERTY, generateProperty());

        final Cursor cursor = mContentResolver.query(ContentUris
                .withAppendedId(PropertyContentProvider.URI_PROPERTY, USER_ID),
                null, null, null, null);
        assertThat(cursor, notNullValue());
        assertThat(cursor.getCount(), is(1));
        assertThat(cursor.moveToFirst(), is(true));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("description")), is("Une super description"));
    }

    private ContentValues generateProperty(){
        final ContentValues values = new ContentValues();
        values.put("userId", 1);
        values.put("type", 1);
        values.put("address", "Marne la vallée");
        values.put("description", "Une super description");
        values.put("entryDate", 26042019);
        values.put("price", 402316);
        values.put("area", 218);
        values.put("nbRoom", 7);
        values.put("status", 2);
        values.put("checkboxSchool", false);
        values.put("checkboxShop", false);
        values.put("checkboxParc", true);
        values.put("checkboxPublicTransport", false);

        return values;
    }
}

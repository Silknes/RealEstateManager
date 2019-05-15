package com.openclassrooms.realestatemanager;

import android.arch.persistence.room.Room;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.openclassrooms.realestatemanager.Database.RealEstateManagerDatabase;
import com.openclassrooms.realestatemanager.Provider.PhotoContentProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(AndroidJUnit4.class)
public class PhotoContentProviderTest {
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
                        .withAppendedId(PhotoContentProvider.URI_ITEM, USER_ID),
                null, null, null, null);
        assertThat(cursor, notNullValue());
        assertThat(cursor.getCount(), is(0));
        cursor.close();
    }

    @Test
    public void insertAndGetPhoto(){
        final Uri uri = mContentResolver.insert(PhotoContentProvider.URI_ITEM, generatePhoto());

        final Cursor cursor = mContentResolver.query(ContentUris
                        .withAppendedId(PhotoContentProvider.URI_ITEM, USER_ID),
                null, null, null, null);
        assertThat(cursor, notNullValue());
        assertThat(cursor.getCount(), is(1));
        assertThat(cursor.moveToFirst(), is(true));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("description")), is("Ma description"));
    }

    private ContentValues generatePhoto(){
        String uri = "https://e-immobilier.credit-agricole.fr/var/caeimmo/storage/images/_aliases/imagearticlefull/conseils/marche/y-a-t-il-une-bonne-saison-pour-acheter-un-bien-immobilier/22818-1-fre-FR/Y-a-t-il-une-bonne-saison-pour-acheter-un-bien-immobilier.jpg";

        final ContentValues values = new ContentValues();
        values.put("propertyId", 1);
        values.put("description", "Ma description");
        values.put("uriPhoto", uri);
        values.put("position", 1);

        return values;
    }
}

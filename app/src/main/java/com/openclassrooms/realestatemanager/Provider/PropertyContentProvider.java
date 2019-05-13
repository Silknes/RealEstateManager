package com.openclassrooms.realestatemanager.Provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.openclassrooms.realestatemanager.Database.RealEstateManagerDatabase;
import com.openclassrooms.realestatemanager.Model.Property;

public class PropertyContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.openclassrooms.realestatemanager.provider";
    public static final String TABLE_NAME = Property.class.getSimpleName();
    public static final Uri URI_PROPERTY = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if(getContext() != null){
            final Cursor cursor = RealEstateManagerDatabase.getInstance(getContext()).propertyDao().getPropertiesWithCursor();
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        }
        throw new IllegalArgumentException("Failed to query row for uri " + uri);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return "vnd.android.cursor.property/" + AUTHORITY + "." + TABLE_NAME;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (getContext() != null){
            final long id = RealEstateManagerDatabase.getInstance(getContext()).propertyDao().insertProperty(Property.fromContentValues(values));
            if(id != 0){
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            }
        }
        throw new IllegalArgumentException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        if(getContext() != null){
            final int count = RealEstateManagerDatabase.getInstance(getContext()).propertyDao().deleteProperty(ContentUris.parseId(uri));
            return count;
        }
        throw new IllegalArgumentException("Failed to delete row into " + uri);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        if(getContext() != null){
            final int count = RealEstateManagerDatabase.getInstance(getContext()).propertyDao().updateProperty(Property.fromContentValues(values));
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
        throw new IllegalArgumentException("Failed to update row into " + uri);
    }
}

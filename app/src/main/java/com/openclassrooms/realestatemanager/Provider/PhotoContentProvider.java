package com.openclassrooms.realestatemanager.Provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.openclassrooms.realestatemanager.Database.RealEstateManagerDatabase;
import com.openclassrooms.realestatemanager.Model.Photo;

public class PhotoContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.openclassrooms.realestatemanager.providerphoto";
    public static final String TABLE_NAME = Photo.class.getSimpleName();
    public static final Uri URI_ITEM = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);


    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (getContext() != null){
            final Cursor cursor = RealEstateManagerDatabase.getInstance(getContext()).photoDao().getMainPhotosWithCursor();
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        }

        throw new IllegalArgumentException("Failed to query row for uri " + uri);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return "vnd.android.cursor.photo/" + AUTHORITY + "." + TABLE_NAME;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (getContext() != null){
            final long id = RealEstateManagerDatabase.getInstance(getContext()).photoDao().insertPhoto(Photo.fromContentValues(values));
            if (id != 0){
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            }
        }

        throw new IllegalArgumentException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (getContext() != null){
            return RealEstateManagerDatabase.getInstance(getContext()).photoDao().deletePhoto(ContentUris.parseId(uri));
        }
        throw new IllegalArgumentException("Failed to delete row into " + uri);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (getContext() != null){
            final int count = RealEstateManagerDatabase.getInstance(getContext()).photoDao().updatePhoto(Photo.fromContentValues(values));
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
        throw new IllegalArgumentException("Failed to update row into " + uri);
    }
}

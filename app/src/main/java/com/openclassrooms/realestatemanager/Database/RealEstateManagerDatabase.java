package com.openclassrooms.realestatemanager.Database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;

import com.openclassrooms.realestatemanager.Database.Dao.PhotoDao;
import com.openclassrooms.realestatemanager.Database.Dao.PropertyDao;
import com.openclassrooms.realestatemanager.Database.Dao.UserDao;
import com.openclassrooms.realestatemanager.Model.Photo;
import com.openclassrooms.realestatemanager.Model.Property;
import com.openclassrooms.realestatemanager.Model.User;

@Database(entities = {Property.class, User.class, Photo.class}, version = 1, exportSchema = false)
public abstract class RealEstateManagerDatabase extends RoomDatabase {
    private static volatile RealEstateManagerDatabase INSTANCE;

    public abstract PropertyDao propertyDao();
    public abstract UserDao userDao();
    public abstract PhotoDao photoDao();

    public static RealEstateManagerDatabase getInstance(Context context){
        if(INSTANCE == null){
            synchronized (RealEstateManagerDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RealEstateManagerDatabase.class, "MyDatabase.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static Callback prepopulateDatabase(){
        return new Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);

                ContentValues contentValues = new ContentValues();
                contentValues.put("id", 1);
                contentValues.put("username", "Eliott Dupau");
                contentValues.put("email", "eliott.dupau@gmail.com");
                contentValues.put("password", "E93d9aa753");

                db.insert("User", OnConflictStrategy.IGNORE, contentValues);
            }
        };
    }
}

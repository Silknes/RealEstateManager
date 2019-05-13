package com.openclassrooms.realestatemanager.Util;

import com.openclassrooms.realestatemanager.Model.GeocodingApi.ApiResult;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MapsApiService {
    @GET("geocode/json?")
    Call<ApiResult> getLocationFromAddress(@Query("key")String apiKey,
                                           @Query("address")String address);


    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}

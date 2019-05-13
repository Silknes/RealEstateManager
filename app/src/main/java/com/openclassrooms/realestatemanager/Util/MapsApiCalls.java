package com.openclassrooms.realestatemanager.Util;

import android.support.annotation.Nullable;

import com.openclassrooms.realestatemanager.Model.GeocodingApi.ApiResult;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsApiCalls {
    public interface CallbacksGeocoding{
        void onResponseGeocoding(@Nullable ApiResult apiResult);
        void onFailureGeocoding();
    }

    public static void fetchLocationFromAddress(CallbacksGeocoding callbacksGeocoding, String apiKey, String address){
        final WeakReference<CallbacksGeocoding> callbacksWeakReference = new WeakReference<>(callbacksGeocoding);
        MapsApiService mapsApiService = MapsApiService.retrofit.create(MapsApiService.class);
        Call<ApiResult> call = mapsApiService.getLocationFromAddress(apiKey, address);
        call.enqueue(new Callback<ApiResult>() {
            @Override
            public void onResponse(Call<ApiResult> call, Response<ApiResult> response) {
                if(callbacksWeakReference.get() != null) callbacksWeakReference.get().onResponseGeocoding(response.body());
            }
            @Override
            public void onFailure(Call<ApiResult> call, Throwable t) {
                if(callbacksWeakReference.get() != null)callbacksWeakReference.get().onFailureGeocoding();
            }
        });
    }
}

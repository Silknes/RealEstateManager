package com.openclassrooms.realestatemanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.test.InstrumentationRegistry;

import com.openclassrooms.realestatemanager.Util.Utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilsTest {
    private Context context;

    @Before
    public void setup(){
        context = InstrumentationRegistry.getInstrumentation().getContext();
    }

    @Test
    public void isNetworkAvailableTest(){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        assertEquals(netInfo != null && netInfo.isConnected(), Utils.isInternetAvailable(context));
    }
}

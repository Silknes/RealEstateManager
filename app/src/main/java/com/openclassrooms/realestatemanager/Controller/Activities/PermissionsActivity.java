package com.openclassrooms.realestatemanager.Controller.Activities;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.openclassrooms.realestatemanager.R;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
/*
 * Activity where the user have to accept the permissions
 */

public class PermissionsActivity extends AppCompatActivity {
    private static final int RC_LOCATION_AND_EXTERNAL_STORAGE = 1;
    private String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        this.askPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);

        this.askPermissions();
    }

    @AfterPermissionGranted(RC_LOCATION_AND_EXTERNAL_STORAGE)
    private void askPermissions(){
        if(!EasyPermissions.hasPermissions(this, perms)){
            EasyPermissions.requestPermissions(this, getString(R.string.permission_act_warning_message_accept_permission), RC_LOCATION_AND_EXTERNAL_STORAGE, perms);
            return;
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

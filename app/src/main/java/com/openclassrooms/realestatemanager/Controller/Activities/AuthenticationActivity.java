package com.openclassrooms.realestatemanager.Controller.Activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.openclassrooms.realestatemanager.Controller.Fragments.AuthenticationFragment;
import com.openclassrooms.realestatemanager.Controller.Fragments.RegisterFragment;
import com.openclassrooms.realestatemanager.R;


/*
 * This activity is launch if no users are connected to the app
 * There is two different screens manage by two different fragments
 * First one is AuthenticationFragment that allow the user to login to our app
 * Last one is RegisterFragment that allow the user to sign in to our app
*/
public class AuthenticationActivity extends AppCompatActivity implements AuthenticationFragment.OnButtonClickedListener, RegisterFragment.OnButtonClickedListener{
    private LinearLayout snackbarLinear;
    private FragmentManager fragmentManager;
    private AuthenticationFragment authenticationFragment;
    private RegisterFragment registerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        fragmentManager = getSupportFragmentManager();

        this.configureAndShowDefaultFragment();

        snackbarLinear = findViewById(R.id.activity_authentication_fragment_container);
    }

    /***************************
    **** Managing fragments ****
    ***************************/

    // Method that configure default fragment
    private void configureAndShowDefaultFragment(){
        authenticationFragment = (AuthenticationFragment)
                fragmentManager.findFragmentById(R.id.activity_authentication_fragment_container);
        if(authenticationFragment == null){
            authenticationFragment = new AuthenticationFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.activity_authentication_fragment_container, authenticationFragment)
                    .commit();
        }
    }

    // Method used to swap to an other fragment
    private void configureAndShowFragment(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        transaction.replace(R.id.activity_authentication_fragment_container, fragment)
                .commit();
    }

    // Callback of register button
    @Override
    public void onRegisterButtonClicked() {
        registerFragment = new RegisterFragment();
        this.configureAndShowFragment(registerFragment);
    }

    // Callback of log in button
    @Override
    public void onLogInButtonClicked() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Callback of sign in button
    @Override
    public void onSignInButtonClicked() {
        Snackbar.make(snackbarLinear, R.string.sign_in_succeed, Snackbar.LENGTH_LONG).show();

        authenticationFragment = new AuthenticationFragment();
        this.configureAndShowFragment(authenticationFragment);
    }

    /**********************
    **** Other methods ****
    **********************/

    // Enable onBackPressed only if displayed fragment is RegisterFragment
    @Override
    public void onBackPressed() {
        if(fragmentManager.getFragments().contains(registerFragment)) {
            authenticationFragment = new AuthenticationFragment();
            this.configureAndShowFragment(authenticationFragment);
        }
    }
}

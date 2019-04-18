package com.openclassrooms.realestatemanager.Controller.Fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.openclassrooms.realestatemanager.Injections.Injection;
import com.openclassrooms.realestatemanager.Injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.Model.User;
import com.openclassrooms.realestatemanager.PropertyViewModel;
import com.openclassrooms.realestatemanager.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AuthenticationFragment extends Fragment {
    private Button btnRegister, btnLogIn, btnValidateRegistration;
    private EditText editEmail, editPassword, editUsername;
    private LinearLayout snackbarLinear;
    private String email, password, username;
    private boolean isEmailEdit, isPasswordEdit, isUsernameEdit;
    private User user;
    private PropertyViewModel propertyViewModel;

    public AuthenticationFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authentication, container, false);

        btnRegister = view.findViewById(R.id.fragment_authentication_btn_register);
        btnLogIn = view.findViewById(R.id.fragment_authentication_btn_login);
        btnValidateRegistration = view.findViewById(R.id.fragment_authentication_btn_validate_registration);

        editEmail = view.findViewById(R.id.fragment_authentication_edit_email);
        editPassword = view.findViewById(R.id.fragment_authentication_edit_password);
        editUsername = view.findViewById(R.id.fragment_authentication_edit_username);

        snackbarLinear = view.findViewById(R.id.fragment_authentication_snackbar_container);

        this.configureViewModel();

        btnRegister.setOnClickListener(v -> {
            btnRegister.setVisibility(View.GONE);
            btnLogIn.setVisibility(View.GONE);
            btnValidateRegistration.setVisibility(View.VISIBLE);
            editUsername.setVisibility(View.VISIBLE);
        });

        this.addTextWatcher(editEmail);
        this.addTextWatcher(editPassword);
        this.addTextWatcher(editUsername);

        btnValidateRegistration.setOnClickListener(v -> {
            user = new User(username, email, password);
            propertyViewModel.createUser(user);

            Snackbar.make(snackbarLinear, "Registration succed !", Snackbar.LENGTH_LONG).show();

            btnRegister.setVisibility(View.VISIBLE);
            btnLogIn.setVisibility(View.VISIBLE);
            btnValidateRegistration.setVisibility(View.GONE);
            editUsername.setVisibility(View.GONE);

            editUsername.setText("");
            editPassword.setText("");
            editEmail.setText("");
        });

        btnLogIn.setOnClickListener(v -> {
            this.propertyViewModel.getUserToLogIn(email, password).observe(this, this::checkEmailAndPassword);
        });

        return view;
    }

    private void checkEmailAndPassword(User user){
        if(user != null){
            //this.propertyViewModel.init(user.getId());
            Toast.makeText(getContext(), "Email & Password correct", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(getContext(), "Email Incorrect", Toast.LENGTH_SHORT).show();
    }

    private void configureViewModel(){
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(getContext());
        this.propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel.class);
    }

    private void addTextWatcher(final EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count) {
                switch(editText.getId()){
                    case R.id.fragment_authentication_edit_email:
                        if(str.toString().trim().length() != 0) {
                            email = str.toString().trim();
                            isEmailEdit = true;
                        } else isEmailEdit = false;
                        break;
                    case R.id.fragment_authentication_edit_password:
                        if(str.toString().trim().length() != 0) {
                            password = str.toString().trim();
                            isPasswordEdit = true;
                        } else isPasswordEdit = false;
                        break;
                    case R.id.fragment_authentication_edit_username:
                        if(str.toString().trim().length() != 0) {
                            username = str.toString().trim();
                            isUsernameEdit = true;
                        } else isUsernameEdit = false;
                        break;
                }
                btnValidateRegistration.setEnabled(isRegisterFormComplete());
                btnLogIn.setEnabled(isLogInFormComplete());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private boolean isRegisterFormComplete(){
        return isEmailEdit && isPasswordEdit && isUsernameEdit;
    }

    private boolean isLogInFormComplete(){
        return isEmailEdit && isPasswordEdit;
    }

}

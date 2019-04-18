package com.openclassrooms.realestatemanager.Controller.Activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.openclassrooms.realestatemanager.Injections.Injection;
import com.openclassrooms.realestatemanager.Injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.Model.User;
import com.openclassrooms.realestatemanager.PropertyViewModel;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.Util.Utils;

public class AuthenticationActivity extends AppCompatActivity {
    private Button btnRegister, btnLogIn, btnValidateRegistration;
    private EditText editEmail, editPassword, editUsername;
    private LinearLayout snackbarLinear;
    private String email, password, username;
    private boolean isEmailEdit, isPasswordEdit, isUsernameEdit;
    private User user;
    private PropertyViewModel propertyViewModel;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        btnRegister = findViewById(R.id.activity_authentication_btn_register);
        btnLogIn = findViewById(R.id.activity_authentication_btn_login);
        btnValidateRegistration = findViewById(R.id.activity_authentication_btn_validate_registration);

        editEmail = findViewById(R.id.activity_authentication_edit_email);
        editPassword = findViewById(R.id.activity_authentication_edit_password);
        editUsername = findViewById(R.id.activity_authentication_edit_username);

        snackbarLinear = findViewById(R.id.activity_authentication_snackbar_container);

        SharedPreferences sharedPreferences = this.getSharedPreferences("MY_SHARED_PREFERENCES", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        this.configureViewModel();

        this.isBtnEnabled(btnRegister);
        this.isBtnEnabled(btnLogIn);
        this.isBtnEnabled(btnValidateRegistration);

        btnRegister.setOnClickListener(v -> {
            btnRegister.setVisibility(View.GONE);
            btnLogIn.setVisibility(View.GONE);
            btnValidateRegistration.setVisibility(View.VISIBLE);
            editUsername.setVisibility(View.VISIBLE);

            editUsername.setText("");
            editPassword.setText("");
            editEmail.setText("");
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
    }

    private void checkEmailAndPassword(User user){
        if(user != null){
            editor.putLong("userId", user.getId()).apply();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else Toast.makeText(this, "Email Incorrect", Toast.LENGTH_SHORT).show();
    }

    private void configureViewModel(){
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(this);
        this.propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel.class);
    }

    private void addTextWatcher(final EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count) {
                switch(editText.getId()){
                    case R.id.activity_authentication_edit_email:
                        if(str.toString().trim().length() != 0 && Utils.isEmailCorrect(str.toString())) {
                            email = str.toString().trim();
                            isEmailEdit = true;
                        } else isEmailEdit = false;
                        break;
                    case R.id.activity_authentication_edit_password:
                        if(str.toString().trim().length() != 0 && Utils.isPasswordCorrect(str.toString())) {
                            password = str.toString().trim();
                            isPasswordEdit = true;
                        } else isPasswordEdit = false;
                        break;
                    case R.id.activity_authentication_edit_username:
                        if(str.toString().trim().length() != 0 && Utils.isUsernameCorrect(str.toString())) {
                            username = str.toString().trim();
                            isUsernameEdit = true;
                        } else isUsernameEdit = false;
                        break;
                }
                btnValidateRegistration.setEnabled(isRegisterFormComplete());
                isBtnEnabled(btnValidateRegistration);
                btnLogIn.setEnabled(isLogInFormComplete());
                isBtnEnabled(btnLogIn);
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

    @Override
    public void onBackPressed() {
        btnRegister.setVisibility(View.VISIBLE);
        btnLogIn.setVisibility(View.VISIBLE);
        btnValidateRegistration.setVisibility(View.GONE);
        editUsername.setVisibility(View.GONE);

        editUsername.setText("");
        editPassword.setText("");
        editEmail.setText("");
    }

    private void isBtnEnabled(Button button){
        if(button.isEnabled()) {
            button.setBackground(getResources().getDrawable(R.drawable.custom_button_enabled));
            button.setTextColor(getResources().getColor(R.color.allowText));
        }
        else {
            button.setBackground(getResources().getDrawable(R.drawable.custom_button_disabled));
            button.setTextColor(getResources().getColor(R.color.white));
        }
    }
}

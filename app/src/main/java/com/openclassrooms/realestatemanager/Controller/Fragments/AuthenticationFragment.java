package com.openclassrooms.realestatemanager.Controller.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.openclassrooms.realestatemanager.Injections.Injection;
import com.openclassrooms.realestatemanager.Injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.Model.User;
import com.openclassrooms.realestatemanager.View.PropertyViewModel;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.Util.Utils;

import java.util.Objects;

public class AuthenticationFragment extends Fragment {
    private TextView wrongLogInTxt; // TextView which display an error message if wrong email or password
    private String mailValue = "", passwordValue = ""; // Both variables receive a user input
    private PropertyViewModel propertyViewModel;

    private OnButtonClickedListener callback; // Use to create a callback to parent activity

    public interface OnButtonClickedListener{
        void onRegisterButtonClicked();
        void onLogInButtonClicked();
    }

    public AuthenticationFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authentication, container, false);

        Button registerButton = view.findViewById(R.id.fragment_authentication_btn_register);
        Button logInButton = view.findViewById(R.id.fragment_authentication_btn_login);
        EditText mailView = view.findViewById(R.id.fragment_authentication_edit_email);
        EditText passwordView = view.findViewById(R.id.fragment_authentication_edit_password);
        wrongLogInTxt = view.findViewById(R.id.fragment_authentication_txt_wrong_log_in);

        this.configureViewModel();

        this.addTextWatcher(mailView);
        this.addTextWatcher(passwordView);

        this.addOnButtonClickListener(registerButton);
        this.addOnButtonClickListener(logInButton);

        return view;
    }

    // Configuring our ViewModel to access to DB
    private void configureViewModel(){
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(getContext());
        this.propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel.class);
    }

    /***************************************************
    **** Manage registerCallback to parent activity ****
    ***************************************************/

    // Create callback when we are sure fragment is attach to parent activity
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.createCallbackToParentActivity();
    }

    // Method that create our callback
    private void createCallbackToParentActivity(){
        callback = (OnButtonClickedListener) getActivity();
    }

    /***************************
    **** And action to view ****
    ***************************/

    // Method that add a listener to a button
    private void addOnButtonClickListener(Button button){
        button.setOnClickListener(v -> actionForEachButton(button));
    }

    // Define actions to do for each button which implements an OnClickListener
    private void actionForEachButton(Button button){
        switch(button.getId()){
            case R.id.fragment_authentication_btn_register:
                callback.onRegisterButtonClicked();
                break;
            case R.id.fragment_authentication_btn_login:
                this.propertyViewModel.getUserToLogIn(mailValue, passwordValue).observe(this, this::checkEmailAndPassword);
                break;
        }
    }

    // Method call when user click on LogInButton and set the value of userId in app Preferences
    private void checkEmailAndPassword(User user){
        if(user != null){
            SharedPreferences.Editor editor = Objects.requireNonNull(getActivity()).getSharedPreferences("MY_SHARED_PREFERENCES", Context.MODE_PRIVATE).edit();
            editor.putLong("userId", user.getId()).apply();
            callback.onLogInButtonClicked();
        } else wrongLogInTxt.setVisibility(View.VISIBLE);
    }

    // Method that add a TextWatcher to an EditText
    private void addTextWatcher(final EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence inputValue, int start, int before, int count) {
                getUserInput(editText, inputValue);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Define what to do with user input for each EditText
    private void getUserInput(EditText editText, CharSequence inputValue){
        if(inputValue.toString().trim().length() != 0){
            switch(editText.getId()){
                case R.id.fragment_authentication_edit_email:
                    if(Utils.isEmailCorrect(inputValue.toString())) mailValue = inputValue.toString();
                    break;
                case R.id.fragment_authentication_edit_password:
                    if(Utils.isPasswordCorrect(inputValue.toString())) passwordValue = inputValue.toString();
                    break;
            }
        }
    }
}

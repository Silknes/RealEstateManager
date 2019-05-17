package com.openclassrooms.realestatemanager.Controller.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.Util.Utils;

public class MortgageSimulatorFragment extends Fragment {
    private TextView monthlyView, txtMonthly;
    private double amountValue, rateValue;
    private int durationValue;

    public MortgageSimulatorFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mortgage_simulator, container, false);

        TextInputEditText rateView = view.findViewById(R.id.fragment_mortgage_simulator_input_edit_rate);
        TextInputEditText durationView = view.findViewById(R.id.fragment_mortgage_simulator_input_edit_duration);
        TextInputEditText amountView = view.findViewById(R.id.fragment_mortgage_simulator_input_edit_amount);
        monthlyView = view.findViewById(R.id.fragment_mortgage_simulator_txt_monthly);
        txtMonthly = view.findViewById(R.id.fragment_mortgage_simulator_txt);

        if(getArguments() != null) amountValue = getArguments().getDouble("amount", 0);
        amountView.setText("" + amountValue);

        this.addTextWatcher(rateView);
        this.addTextWatcher(durationView);
        this.addTextWatcher(amountView);

        return view;
    }

    // Method that add a TextWatcher to an EditText
    private void addTextWatcher(final TextInputEditText inputView){
        inputView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence inputValue, int start, int before, int count) {
                getUserInput(inputView, inputValue);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Define what to do with user input for each EditText
    private void getUserInput(TextInputEditText inputView, CharSequence inputValue){
        if(inputValue.toString().trim().length() != 0){
            switch(inputView.getId()){
                case R.id.fragment_mortgage_simulator_input_edit_rate:
                    rateValue = Double.parseDouble(inputValue.toString());
                    break;
                case R.id.fragment_mortgage_simulator_input_edit_duration:
                    durationValue = Integer.parseInt(inputValue.toString());
                    break;
                case R.id.fragment_mortgage_simulator_input_edit_amount:
                    amountValue = Double.parseDouble(inputValue.toString());
                    break;
            }
            this.calculateMonthly();
        }
    }

    private void calculateMonthly(){
        if(amountValue != 0 && durationValue != 0 && rateValue != 0){
            monthlyView.setText(Utils.calculateMonthly(amountValue, rateValue, durationValue));
            txtMonthly.setVisibility(View.VISIBLE);
        } else txtMonthly.setVisibility(View.GONE);
    }
}

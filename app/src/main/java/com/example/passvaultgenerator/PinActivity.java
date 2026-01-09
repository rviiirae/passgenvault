package com.example.passvaultgenerator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PinActivity extends AppCompatActivity {

    private static final String PIN_KEY = "app_pin";
    private SharedPreferences sharedPreferences;
    private final EditText[] pinDigits = new EditText[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        sharedPreferences = getSharedPreferences("pin_prefs", MODE_PRIVATE);

        TextView titleTextView = findViewById(R.id.pin_title_textview);
        Button submitButton = findViewById(R.id.pin_submit_button);

        pinDigits[0] = findViewById(R.id.pin_digit_1);
        pinDigits[1] = findViewById(R.id.pin_digit_2);
        pinDigits[2] = findViewById(R.id.pin_digit_3);
        pinDigits[3] = findViewById(R.id.pin_digit_4);
        pinDigits[4] = findViewById(R.id.pin_digit_5);
        pinDigits[5] = findViewById(R.id.pin_digit_6);

        setupTextWatchers();
        setupBackEvents();

        if (isPinSet()) {
            titleTextView.setText("Enter PIN to Unlock");
            submitButton.setText("Unlock");
        } else {
            titleTextView.setText("Set a 6-Digit PIN");
            submitButton.setText("Set PIN");
        }

        submitButton.setOnClickListener(v -> {
            String enteredPin = getEnteredPin();
            if (enteredPin.length() < 6) {
                Toast.makeText(this, "Please enter all 6 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isPinSet()) {
                String savedPin = sharedPreferences.getString(PIN_KEY, "");
                if (enteredPin.equals(savedPin)) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
                    clearPin();
                }
            } else {
                sharedPreferences.edit().putString(PIN_KEY, enteredPin).apply();
                Toast.makeText(this, "PIN Set Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });
    }

    private void setupTextWatchers() {
        for (int i = 0; i < 6; i++) {
            final int index = i;
            pinDigits[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1) {
                        // Force transformation refresh instantly
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            if (index < 5) {
                                pinDigits[index + 1].requestFocus();
                            }
                        }, 10);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void setupBackEvents() {
        for (int i = 1; i < 6; i++) {
            final int index = i;
            pinDigits[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (pinDigits[index].getText().toString().isEmpty()) {
                        pinDigits[index - 1].requestFocus();
                        pinDigits[index - 1].setText("");
                        return true;
                    }
                }
                return false;
            });
        }
    }

    private String getEnteredPin() {
        StringBuilder pin = new StringBuilder();
        for (EditText et : pinDigits) {
            pin.append(et.getText().toString());
        }
        return pin.toString();
    }

    private void clearPin() {
        for (EditText et : pinDigits) {
            et.setText("");
        }
        pinDigits[0].requestFocus();
    }

    private boolean isPinSet() {
        return sharedPreferences.contains(PIN_KEY);
    }
}

package com.example.passvaultgenerator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PinActivity extends AppCompatActivity {

    private static final String PIN_KEY = "app_pin";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        // Use a separate, simple SharedPreferences for the PIN
        sharedPreferences = getSharedPreferences("pin_prefs", MODE_PRIVATE);

        TextView titleTextView = findViewById(R.id.pin_title_textview);
        EditText pinEditText = findViewById(R.id.pin_edit_text);
        Button submitButton = findViewById(R.id.pin_submit_button);

        if (isPinSet()) {
            titleTextView.setText("Enter PIN to Unlock");
        } else {
            titleTextView.setText("Set a New PIN");
        }

        submitButton.setOnClickListener(v -> {
            String enteredPin = pinEditText.getText().toString();
            if (enteredPin.length() < 4) {
                Toast.makeText(this, "PIN must be at least 4 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isPinSet()) {
                // Check PIN
                String savedPin = sharedPreferences.getString(PIN_KEY, "");
                if (enteredPin.equals(savedPin)) {
                    // Correct PIN
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    // Incorrect PIN
                    Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Set new PIN
                sharedPreferences.edit().putString(PIN_KEY, enteredPin).apply();
                Toast.makeText(this, "PIN Set Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });
    }

    private boolean isPinSet() {
        return sharedPreferences.contains(PIN_KEY);
    }
}

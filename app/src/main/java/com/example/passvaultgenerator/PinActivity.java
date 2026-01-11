package com.example.passvaultgenerator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.Locale;
import java.util.concurrent.Executor;

public class PinActivity extends AppCompatActivity {

    private static final String PIN_KEY = "app_pin";
    private static final String FAILED_ATTEMPTS_KEY = "failed_attempts";
    private static final String LOCKOUT_END_TIME_KEY = "lockout_end_time";
    private static final String KEY_BIOMETRIC = "biometric_enabled";
    
    private SharedPreferences sharedPreferences;
    private final EditText[] pinDigits = new EditText[6];
    private TextView titleTextView;
    private Button submitButton;
    private CountDownTimer lockoutTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Privacy: Prevent Screenshots & App Switcher Preview
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_pin);

        sharedPreferences = getSharedPreferences("pin_prefs", MODE_PRIVATE);

        titleTextView = findViewById(R.id.pin_title_textview);
        submitButton = findViewById(R.id.pin_submit_button);

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
            
            // Biometric Fallback (if enabled in settings)
            SharedPreferences settingsPrefs = getSharedPreferences("app_settings", MODE_PRIVATE);
            if (settingsPrefs.getBoolean(KEY_BIOMETRIC, false)) {
                showBiometricPrompt();
            }
        } else {
            titleTextView.setText("Set a 6-Digit PIN");
            submitButton.setText("Set PIN");
        }

        checkLockout();

        submitButton.setOnClickListener(v -> {
            String enteredPin = getEnteredPin();
            if (enteredPin.length() < 6) {
                Toast.makeText(this, "Please enter all 6 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isPinSet()) {
                String savedPin = sharedPreferences.getString(PIN_KEY, "");
                if (enteredPin.equals(savedPin)) {
                    onUnlockSuccess();
                } else {
                    handleFailedAttempt();
                }
            } else {
                sharedPreferences.edit().putString(PIN_KEY, enteredPin).apply();
                Toast.makeText(this, "PIN Set Successfully", Toast.LENGTH_SHORT).show();
                onUnlockSuccess();
            }
        });
    }

    private void onUnlockSuccess() {
        sharedPreferences.edit().putInt(FAILED_ATTEMPTS_KEY, 0).apply();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(PinActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                onUnlockSuccess();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Unlock")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use PIN")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void handleFailedAttempt() {
        int failedAttempts = sharedPreferences.getInt(FAILED_ATTEMPTS_KEY, 0) + 1;
        sharedPreferences.edit().putInt(FAILED_ATTEMPTS_KEY, failedAttempts).apply();

        if (failedAttempts >= 3) {
            long lockoutMillis = (failedAttempts == 3) ? 60000 : 60000 + (long) (failedAttempts - 3) * 3 * 60000;
            long endTime = System.currentTimeMillis() + lockoutMillis;
            sharedPreferences.edit().putLong(LOCKOUT_END_TIME_KEY, endTime).apply();
            startLockoutTimer(lockoutMillis);
        } else {
            Toast.makeText(this, "Incorrect PIN. " + (3 - failedAttempts) + " attempts left.", Toast.LENGTH_SHORT).show();
            clearPin();
        }
    }

    private void checkLockout() {
        long endTime = sharedPreferences.getLong(LOCKOUT_END_TIME_KEY, 0);
        long currentTime = System.currentTimeMillis();
        if (currentTime < endTime) {
            startLockoutTimer(endTime - currentTime);
        }
    }

    private void startLockoutTimer(long millisInFuture) {
        setEnabledUI(false);
        if (lockoutTimer != null) lockoutTimer.cancel();
        lockoutTimer = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                titleTextView.setText(String.format(Locale.getDefault(), "Try again in %02d:%02d", minutes, seconds));
            }
            @Override
            public void onFinish() {
                titleTextView.setText("Enter PIN to Unlock");
                setEnabledUI(true);
                clearPin();
            }
        }.start();
    }

    private void setEnabledUI(boolean enabled) {
        submitButton.setEnabled(enabled);
        for (EditText et : pinDigits) et.setEnabled(enabled);
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
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            if (index < 5) pinDigits[index + 1].requestFocus();
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
        for (EditText et : pinDigits) pin.append(et.getText().toString());
        return pin.toString();
    }

    private void clearPin() {
        for (EditText et : pinDigits) et.setText("");
        pinDigits[0].requestFocus();
    }

    private boolean isPinSet() {
        return sharedPreferences.contains(PIN_KEY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lockoutTimer != null) lockoutTimer.cancel();
    }
}

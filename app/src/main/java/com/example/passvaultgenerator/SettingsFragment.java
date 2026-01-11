package com.example.passvaultgenerator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_PURE_DARK = "pure_dark";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        SwitchMaterial pureDarkSwitch = view.findViewById(R.id.pure_dark_switch);
        Button clearVaultButton = view.findViewById(R.id.clear_vault_button);

        pureDarkSwitch.setChecked(sharedPreferences.getBoolean(KEY_PURE_DARK, false));

        pureDarkSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(KEY_PURE_DARK, isChecked).apply();
            // Restart the app to apply the theme globally
            new AlertDialog.Builder(getContext())
                    .setTitle("Theme Change")
                    .setMessage("The app needs to restart to apply the OLED theme correctly.")
                    .setPositiveButton("Restart Now", (dialog, which) -> {
                        Intent i = getActivity().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        getActivity().finish();
                    })
                    .setNegativeButton("Later", null)
                    .show();
        });

        clearVaultButton.setOnClickListener(v -> showPinConfirmationDialog());

        return view;
    }

    private void showPinConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Verify PIN");
        builder.setMessage("Please enter your 6-digit PIN to confirm clearing the vault.");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String enteredPin = input.getText().toString();
            SharedPreferences pinPrefs = getActivity().getSharedPreferences("pin_prefs", Context.MODE_PRIVATE);
            String savedPin = pinPrefs.getString("app_pin", "");

            if (enteredPin.equals(savedPin)) {
                PasswordStorage storage = new PasswordStorage(getContext());
                storage.saveVaultItems(new ArrayList<>());
                Toast.makeText(getContext(), "Vault cleared successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Incorrect PIN. Vault not cleared.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}

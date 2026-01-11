package com.example.passvaultgenerator;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_PURE_DARK = "pure_dark";
    private static final String KEY_BIOMETRIC = "biometric_enabled";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        SwitchMaterial pureDarkSwitch = view.findViewById(R.id.pure_dark_switch);
        SwitchMaterial biometricSwitch = view.findViewById(R.id.biometric_switch);
        Button exportButton = view.findViewById(R.id.export_button);
        Button importButton = view.findViewById(R.id.import_button);
        Button clearVaultButton = view.findViewById(R.id.clear_vault_button);

        pureDarkSwitch.setChecked(sharedPreferences.getBoolean(KEY_PURE_DARK, false));
        if (biometricSwitch != null) {
            biometricSwitch.setChecked(sharedPreferences.getBoolean(KEY_BIOMETRIC, false));
            biometricSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                sharedPreferences.edit().putBoolean(KEY_BIOMETRIC, isChecked).apply();
            });
        }

        pureDarkSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(KEY_PURE_DARK, isChecked).apply();
            showRestartDialog();
        });

        exportButton.setOnClickListener(v -> exportVault());
        importButton.setOnClickListener(v -> showImportDialog());
        clearVaultButton.setOnClickListener(v -> showPinConfirmationDialog());

        return view;
    }

    private void showRestartDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Theme Change")
                .setMessage("The app needs to restart to apply the OLED theme correctly.")
                .setPositiveButton("Restart Now", (dialog, which) -> {
                    Intent i = getActivity().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName());
                    if (i != null) {
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        getActivity().finish();
                    }
                })
                .setNegativeButton("Later", null)
                .show();
    }

    private void exportVault() {
        PasswordStorage storage = new PasswordStorage(getContext());
        List<VaultItem> items = storage.getVaultItems();
        String json = new Gson().toJson(items);
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("vault_export", json);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), "Vault data copied to clipboard!", Toast.LENGTH_LONG).show();
    }

    private void showImportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Import Vault");
        builder.setMessage("Paste your exported vault string below:");

        final EditText input = new EditText(getContext());
        input.setHint("Paste here...");
        builder.setView(input);

        builder.setPositiveButton("Import", (dialog, which) -> {
            try {
                String json = input.getText().toString();
                PasswordStorage storage = new PasswordStorage(getContext());
                if (json.contains("name") && json.contains("username")) {
                    storage.importVaultJson(json);
                    Toast.makeText(getContext(), "Import successful! Restarting...", Toast.LENGTH_SHORT).show();
                    showRestartDialog();
                } else {
                    Toast.makeText(getContext(), "Invalid vault data", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Import failed", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showPinConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Verify PIN");
        builder.setMessage("Enter your 6-digit PIN to confirm clearing the vault.");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
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
                Toast.makeText(getContext(), "Incorrect PIN", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}

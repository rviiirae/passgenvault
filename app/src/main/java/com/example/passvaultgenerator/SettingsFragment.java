package com.example.passvaultgenerator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

        clearVaultButton.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Danger Zone")
                    .setMessage("This will permanently delete all saved passwords. Are you sure?")
                    .setPositiveButton("Clear All", (dialog, which) -> {
                        PasswordStorage storage = new PasswordStorage(getContext());
                        storage.saveVaultItems(new ArrayList<>());
                        Toast.makeText(getContext(), "Vault cleared", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return view;
    }
}

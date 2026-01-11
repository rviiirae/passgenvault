package com.example.passvaultgenerator;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class PasswordStorage {

    private static final String TAG = "PasswordStorage";
    private SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();

    public PasswordStorage(@NonNull Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    "secret_shared_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Error creating EncryptedSharedPreferences", e);
        }
    }

    public void saveVaultItems(List<VaultItem> vaultItems) {
        if (sharedPreferences == null) return;
        String json = gson.toJson(vaultItems);
        sharedPreferences.edit().putString("vault_items", json).apply();
    }

    public void importVaultJson(String json) {
        if (sharedPreferences == null) return;
        sharedPreferences.edit().putString("vault_items", json).apply();
    }

    @NonNull
    public List<VaultItem> getVaultItems() {
        if (sharedPreferences == null) {
            return new ArrayList<>();
        }
        String json = sharedPreferences.getString("vault_items", null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<VaultItem>>() {}.getType();
        return gson.fromJson(json, type);
    }
}

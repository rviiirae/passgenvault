package com.example.passvaultgenerator;

import androidx.annotation.NonNull;

public class VaultItem {
    private final String name;
    private final String username;

    public VaultItem(@NonNull String name, @NonNull String username) {
        this.name = name;
        this.username = username;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getUsername() {
        return username;
    }
}

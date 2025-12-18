package com.example.passvaultgenerator;

import androidx.annotation.NonNull;

public class VaultItem {
    private final String name;
    private final String username;
    private final String password;

    public VaultItem(@NonNull String name, @NonNull String username, @NonNull String password) {
        this.name = name;
        this.username = username;
        this.password = password;
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

package com.example.passvaultgenerator;

import androidx.annotation.NonNull;

public class VaultItem {
    private final String name;
    private final String username;
    private final String password;
    private final long lastChangedTimestamp;

    public VaultItem(@NonNull String name, @NonNull String username, @NonNull String password) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.lastChangedTimestamp = System.currentTimeMillis();
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    public long getLastChangedTimestamp() {
        return lastChangedTimestamp;
    }
}

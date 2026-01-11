package com.example.passvaultgenerator;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VaultFragment extends Fragment implements VaultItemAdapter.OnItemDeleteListener {

    private EditText nameEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText searchEditText;

    private PasswordStorage passwordStorage;
    private List<VaultItem> vaultItems;
    private VaultItemAdapter vaultItemAdapter;
    private final Handler updateHandler = new Handler(Looper.getMainLooper());
    private Runnable updateRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vault, container, false);

        passwordStorage = new PasswordStorage(getContext());
        vaultItems = passwordStorage.getVaultItems();

        nameEditText = view.findViewById(R.id.name_edit_text);
        usernameEditText = view.findViewById(R.id.username_edit_text);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        searchEditText = view.findViewById(R.id.search_edit_text);
        RecyclerView vaultRecyclerView = view.findViewById(R.id.vault_recycler_view);
        Button saveButton = view.findViewById(R.id.save_button);

        vaultItemAdapter = new VaultItemAdapter(vaultItems, this);
        vaultRecyclerView.setAdapter(vaultItemAdapter);
        vaultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            for (VaultItem item : vaultItems) {
                if (item.getName().equalsIgnoreCase(name) && item.getUsername().equalsIgnoreCase(username)) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Duplicate Warning")
                            .setMessage("An entry with the same name and username already exists. Are you sure you want to save another?")
                            .setPositiveButton("Save", (dialog, which) -> saveNewItem(name, username, password))
                            .setNegativeButton("Cancel", null)
                            .show();
                    return; 
                }
            }
            saveNewItem(name, username, password);
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                vaultItemAdapter.filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        updateRunnable = new Runnable() {
            @Override
            public void run() {
                vaultItemAdapter.notifyDataSetChanged();
                updateHandler.postDelayed(this, 10000);
            }
        };
        updateHandler.post(updateRunnable);

        return view;
    }

    private void saveNewItem(String name, String username, String password) {
        VaultItem newItem = new VaultItem(name, username, password);
        vaultItems.add(newItem);
        vaultItemAdapter.updateList(vaultItems);
        passwordStorage.saveVaultItems(vaultItems);

        nameEditText.setText("");
        usernameEditText.setText("");
        passwordEditText.setText("");
        searchEditText.setText(""); // Clear search on save
    }

    @Override
    public void onItemDelete(int position) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete_confirmation_title)
                .setMessage(R.string.delete_confirmation_message)
                .setPositiveButton(R.string.delete_button_label, (dialog, which) -> {
                    vaultItems.remove(position);
                    vaultItemAdapter.updateList(vaultItems);
                    passwordStorage.saveVaultItems(vaultItems);
                })
                .setNegativeButton(R.string.cancel_button_label, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        updateHandler.removeCallbacks(updateRunnable);
    }
}

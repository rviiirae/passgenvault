package com.example.passvaultgenerator;

import android.app.AlertDialog;
import android.os.Bundle;
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

    private PasswordStorage passwordStorage;
    private List<VaultItem> vaultItems;
    private VaultItemAdapter vaultItemAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vault, container, false);

        passwordStorage = new PasswordStorage(getContext());
        vaultItems = passwordStorage.getVaultItems();

        nameEditText = view.findViewById(R.id.name_edit_text);
        usernameEditText = view.findViewById(R.id.username_edit_text);
        passwordEditText = view.findViewById(R.id.password_edit_text);
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

            // Check for duplicates before saving
            for (VaultItem item : vaultItems) {
                if (item.getName().equalsIgnoreCase(name) && item.getUsername().equalsIgnoreCase(username)) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Duplicate Warning")
                            .setMessage("An entry with the same name and username already exists. Are you sure you want to save another?")
                            .setPositiveButton("Save", (dialog, which) -> saveNewItem(name, username, password))
                            .setNegativeButton("Cancel", null)
                            .show();
                    return; // Stop further execution until user decides
                }
            }
            saveNewItem(name, username, password);
        });

        return view;
    }

    private void saveNewItem(String name, String username, String password) {
        VaultItem newItem = new VaultItem(name, username, password);
        vaultItems.add(newItem);
        vaultItemAdapter.notifyItemInserted(vaultItems.size() - 1);
        passwordStorage.saveVaultItems(vaultItems);

        nameEditText.setText("");
        usernameEditText.setText("");
        passwordEditText.setText("");
    }

    @Override
    public void onItemDelete(int position) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete_confirmation_title)
                .setMessage(R.string.delete_confirmation_message)
                .setPositiveButton(R.string.delete_button_label, (dialog, which) -> {
                    vaultItems.remove(position);
                    vaultItemAdapter.notifyItemRemoved(position);
                    passwordStorage.saveVaultItems(vaultItems);
                })
                .setNegativeButton(R.string.cancel_button_label, null)
                .show();
    }
}

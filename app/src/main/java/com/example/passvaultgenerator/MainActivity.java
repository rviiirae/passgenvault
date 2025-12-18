package com.example.passvaultgenerator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;

    private PasswordStorage passwordStorage;
    private List<VaultItem> vaultItems;
    private VaultItemAdapter vaultItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        passwordStorage = new PasswordStorage(this);
        vaultItems = passwordStorage.getVaultItems();

        TextView lengthTextView = findViewById(R.id.length_text_view);
        SeekBar lengthSeekBar = findViewById(R.id.length_seek_bar);
        CheckBox lowerCaseCheckBox = findViewById(R.id.lower_case_check_box);
        CheckBox upperCaseCheckBox = findViewById(R.id.upper_case_check_box);
        CheckBox numbersCheckBox = findViewById(R.id.numbers_check_box);
        CheckBox symbolsCheckBox = findViewById(R.id.symbols_check_box);
        TextView generatedPasswordTextView = findViewById(R.id.generated_password_text_view);
        nameEditText = findViewById(R.id.name_edit_text);
        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        RecyclerView vaultRecyclerView = findViewById(R.id.vault_recycler_view);
        Button generateButton = findViewById(R.id.generate_button);
        Button copyButton = findViewById(R.id.copy_button);
        Button quickButton = findViewById(R.id.quick_button);
        Button saveButton = findViewById(R.id.save_button);

        vaultItemAdapter = new VaultItemAdapter(vaultItems);
        vaultRecyclerView.setAdapter(vaultItemAdapter);
        vaultRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        lengthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lengthTextView.setText(getString(R.string.length_label, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        generateButton.setOnClickListener(v -> {
            int length = lengthSeekBar.getProgress();
            boolean useLower = lowerCaseCheckBox.isChecked();
            boolean useUpper = upperCaseCheckBox.isChecked();
            boolean useNumbers = numbersCheckBox.isChecked();
            boolean useSymbols = symbolsCheckBox.isChecked();

            String password = PasswordGenerator.generatePassword(length, useLower, useUpper, useNumbers, useSymbols);
            generatedPasswordTextView.setText(password);
        });

        copyButton.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("password", generatedPasswordTextView.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Password copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String username = usernameEditText.getText().toString();

            if (name.isEmpty() || username.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            VaultItem newItem = new VaultItem(name, username);
            vaultItems.add(newItem);
            vaultItemAdapter.notifyItemInserted(vaultItems.size() - 1);
            passwordStorage.saveVaultItems(vaultItems);

            nameEditText.setText("");
            usernameEditText.setText("");
            passwordEditText.setText("");
        });

        quickButton.setOnClickListener(v -> {
            String generatedPassword = generatedPasswordTextView.getText().toString();
            passwordEditText.setText(generatedPassword);
        });
    }
}

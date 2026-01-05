package com.example.passvaultgenerator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class GeneratorFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generator, container, false);

        TextView lengthTextView = view.findViewById(R.id.length_text_view);
        SeekBar lengthSeekBar = view.findViewById(R.id.length_seek_bar);
        CheckBox lowerCaseCheckBox = view.findViewById(R.id.lower_case_check_box);
        CheckBox upperCaseCheckBox = view.findViewById(R.id.upper_case_check_box);
        CheckBox numbersCheckBox = view.findViewById(R.id.numbers_check_box);
        CheckBox symbolsCheckBox = view.findViewById(R.id.symbols_check_box);
        TextView generatedPasswordTextView = view.findViewById(R.id.generated_password_text_view);
        Button generateButton = view.findViewById(R.id.generate_button);
        Button copyButton = view.findViewById(R.id.copy_button);

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
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("password", generatedPasswordTextView.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Password copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}

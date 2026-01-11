package com.example.passvaultgenerator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class GeneratorFragment extends Fragment {

    private TextView strengthTextView;
    private ProgressBar strengthProgressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generator, container, false);

        TextView lengthTextView = view.findViewById(R.id.length_text_view);
        SeekBar lengthSeekBar = view.findViewById(R.id.length_seek_bar);
        SwitchMaterial lowerCaseSwitch = view.findViewById(R.id.lower_case_switch);
        SwitchMaterial upperCaseSwitch = view.findViewById(R.id.upper_case_switch);
        SwitchMaterial numbersSwitch = view.findViewById(R.id.numbers_switch);
        SwitchMaterial symbolsSwitch = view.findViewById(R.id.symbols_switch);
        TextView generatedPasswordTextView = view.findViewById(R.id.generated_password_text_view);
        Button generateButton = view.findViewById(R.id.generate_button);
        ImageButton copyButton = view.findViewById(R.id.copy_button);
        
        strengthTextView = view.findViewById(R.id.strength_text_view);
        strengthProgressBar = view.findViewById(R.id.strength_progress_bar);

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
            boolean useLower = lowerCaseSwitch.isChecked();
            boolean useUpper = upperCaseSwitch.isChecked();
            boolean useNumbers = numbersSwitch.isChecked();
            boolean useSymbols = symbolsSwitch.isChecked();

            String password = PasswordGenerator.generatePassword(length, useLower, useUpper, useNumbers, useSymbols);
            generatedPasswordTextView.setText(password);
            updateStrengthMeter(password, useLower, useUpper, useNumbers, useSymbols);
        });

        copyButton.setOnClickListener(v -> {
            if (getActivity() == null) return;
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("password", generatedPasswordTextView.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Password copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void updateStrengthMeter(String password, boolean lower, boolean upper, boolean numbers, boolean symbols) {
        if (password.isEmpty()) {
            strengthTextView.setText("Strength: Unknown");
            strengthProgressBar.setProgress(0);
            return;
        }

        int score = 0;
        int length = password.length();

        if (length >= 8) score++;
        if (length >= 12) score++;
        
        int types = 0;
        if (lower) types++;
        if (upper) types++;
        if (numbers) types++;
        if (symbols) types++;
        
        if (types >= 3) score++;
        if (types == 4 && length >= 14) score++;

        if (score <= 1) {
            strengthTextView.setText("Strength: Weak");
            strengthTextView.setTextColor(Color.parseColor("#FF4C4C")); // Red
            strengthProgressBar.setProgress(33);
            strengthProgressBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#FF4C4C")));
        } else if (score == 2) {
            strengthTextView.setText("Strength: Medium");
            strengthTextView.setTextColor(Color.parseColor("#FFD700")); // Gold/Yellow
            strengthProgressBar.setProgress(66);
            strengthProgressBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#FFD700")));
        } else {
            strengthTextView.setText("Strength: Strong");
            strengthTextView.setTextColor(Color.parseColor("#4CAF50")); // Green
            strengthProgressBar.setProgress(100);
            strengthProgressBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        }
    }
}

package com.example.sokri.ui.calculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sokri.databinding.FragmentDoseCalculatorBinding;

public class DoseCalculatorFragment extends Fragment {

    private FragmentDoseCalculatorBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDoseCalculatorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.calculateButton.setOnClickListener(v -> calculateDose());

        return root;
    }

    private void calculateDose() {
        try {
            int currentGlucose = Integer.parseInt(binding.currentGlucoseEditText.getText().toString());
            int targetGlucose = Integer.parseInt(binding.targetGlucoseEditText.getText().toString());
            int totalDoses = Integer.parseInt(binding.totalDosesEditText.getText().toString());

            double correctiveDose = (currentGlucose - targetGlucose) / (1700.0 / totalDoses);

            binding.resultTextView.setText(String.format("Dose corrective: %.2f unités", correctiveDose));

            // Vider les champs après le calcul
            binding.currentGlucoseEditText.setText("");
            binding.targetGlucoseEditText.setText("");
            binding.totalDosesEditText.setText("");
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Veuillez entrer des valeurs valides", Toast.LENGTH_SHORT).show();
        }
    }
}

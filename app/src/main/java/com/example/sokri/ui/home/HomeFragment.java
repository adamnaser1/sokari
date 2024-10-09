package com.example.sokri.ui.home;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.DatePicker;
import android.app.TimePickerDialog;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sokri.GlucoseDatabaseHelper;
import com.example.sokri.databinding.FragmentHomeBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private GlucoseDatabaseHelper dbHelper;
    private Calendar selectedDate = Calendar.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dbHelper = new GlucoseDatabaseHelper(getContext());

        EditText glucoseValue = binding.glucoseValue;
        EditText situation = binding.situation;
        EditText insulinDose = binding.insulinDose;
        EditText manualTime = binding.manualTime;
        EditText notes = binding.notes;
        CheckBox automaticTime = binding.automaticTime;
        Button saveButton = binding.saveButton;
        Button selectDateButton = binding.selectDateButton;
        Button selectTimeButton = binding.timeButton;

        selectTimeButton.setOnClickListener(v -> {
            int hour = 0; // Default hour
            int minute = 0; // Default minute

            // If manualTime already has a value, parse it to get the hour and minute
            if (!manualTime.getText().toString().isEmpty()) {
                String[] timeParts = manualTime.getText().toString().split(":");
                if (timeParts.length == 2) {
                    hour = Integer.parseInt(timeParts[0]);
                    minute = Integer.parseInt(timeParts[1]);
                }
            }

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute1) -> {
                // Format the selected time
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                manualTime.setText(formattedTime);
            }, hour, minute, true); // true for 24-hour format
            timePickerDialog.show();
        });
        selectDateButton.setOnClickListener(v -> {
            int year = selectedDate.get(Calendar.YEAR);
            int month = selectedDate.get(Calendar.MONTH);
            int day = selectedDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year1, month1, dayOfMonth) -> {
                selectedDate.set(Calendar.YEAR, year1);
                selectedDate.set(Calendar.MONTH, month1);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                selectDateButton.setText(dateFormat.format(selectedDate.getTime()));
            }, year, month, day);
            datePickerDialog.show();
        });

        automaticTime.setOnCheckedChangeListener((buttonView, isChecked) -> {
            manualTime.setEnabled(!isChecked);
        });

        saveButton.setOnClickListener(v -> {
            String glucose = glucoseValue.getText().toString();
            String situationText = situation.getText().toString();
            String insulin = insulinDose.getText().toString();
            String notesText = notes.getText().toString();
            String time;

            // Récupérer la date au format souhaité
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String selectedDateStr = dateFormat.format(selectedDate.getTime());

            if (automaticTime.isChecked()) {
                time = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
            } else {
                // Vérifiez si l'heure est bien saisie
                String manualTimeText = manualTime.getText().toString();
                if (manualTimeText.isEmpty()) {
                    Toast.makeText(getContext(), "Veuillez saisir l'heure manuellement", Toast.LENGTH_SHORT).show();
                    return;
                }
                time = selectedDateStr + " " + manualTimeText; // Combine la date et l'heure
            }

            if (!glucose.isEmpty() && !insulin.isEmpty() && !time.isEmpty()) {
                saveMeasurement(Integer.parseInt(glucose), time, situationText, insulin, notesText);
                Toast.makeText(getContext(), "Mesure enregistrée avec succès", Toast.LENGTH_SHORT).show();

                // Clear fields after saving
                glucoseValue.setText("");
                situation.setText("");
                insulinDose.setText("");
                manualTime.setText("");
                notes.setText("");
                automaticTime.setChecked(false);
                selectDateButton.setText("Choisir la date");
            } else {
                Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            }
        });


        return root;
    }

    private void saveMeasurement(int glucoseValue, String time, String situation, String insulinDose, String notes) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("glucose_value", glucoseValue);
        values.put("time", time);
        values.put("situation", situation);
        values.put("insulin_dose", insulinDose);
        values.put("notes", notes); // Ajout des notes ici

        long result = db.insert("measurements", null, values);
        if (result == -1) {
            Toast.makeText(getContext(), "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Mesure enregistrée avec ID: " + result, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

package com.example.sokri.ui.trimonthly;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sokri.GlucoseDatabaseHelper;
import com.example.sokri.ReminderBroadcastReceiver;
import com.example.sokri.databinding.FragmentTrimesterBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TrimesterFragment extends Fragment {

    private FragmentTrimesterBinding binding;
    private GlucoseDatabaseHelper dbHelper;
    private TextView savedValuesTextView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTrimesterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dbHelper = new GlucoseDatabaseHelper(getContext());
        savedValuesTextView = binding.savedValuesTextView;

        binding.dateButton.setOnClickListener(v -> showDatePicker());

        binding.saveButton.setOnClickListener(v -> saveData());

        scheduleTrimesterReminder();
        displaySavedValues(); // Display saved values on fragment creation

        return root;
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> binding.dateEditText.setText(String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void saveData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String date = binding.dateEditText.getText().toString();
        String percentage = binding.percentageEditText.getText().toString();

        // Calculate next appointment date
        String nextAppointmentDate = calculateNextAppointmentDate(date);

        // Insert new values into the database, including the next appointment date
        db.execSQL("INSERT INTO quarterly_measurements (date, percentage, next_appointment) VALUES (?, ?, ?)",
                new String[]{date, percentage, nextAppointmentDate});

        Toast.makeText(getContext(), "Données trimestrielles enregistrées", Toast.LENGTH_SHORT).show();
        clearFields();
        displaySavedValues(); // Update displayed values after saving
        binding.nextAppointmentTextView.setText("Prochain rendez-vous: " + nextAppointmentDate); // Show the new appointment date
    }
    private String calculateNextAppointmentDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(date));
            cal.add(Calendar.MONTH, 3); // Add 3 months
            return sdf.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }



    private void clearFields() {
        binding.dateEditText.setText("");
        binding.percentageEditText.setText("");
    }

    private void displaySavedValues() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM quarterly_measurements", null);
        StringBuilder values = new StringBuilder();

        while (cursor.moveToNext()) {
            @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));
            @SuppressLint("Range") String percentage = cursor.getString(cursor.getColumnIndex("percentage"));
            @SuppressLint("Range") String nextAppointment = cursor.getString(cursor.getColumnIndex("next_appointment"));
            values.append(String.format("Date: %s, Pourcentage: %s,\n Prochain rendez-vous: %s\n", date, percentage, nextAppointment));
        }
        cursor.close();

        savedValuesTextView.setText(values.toString());
    }


    private void showNextAppointmentDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(date));
            cal.add(Calendar.MONTH, 3); // Add 3 months
            String nextAppointment = sdf.format(cal.getTime());
            binding.nextAppointmentTextView.setText("Prochain rendez-vous: " + nextAppointment); // Set the text in the TextView
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private void scheduleTrimesterReminder() {
        Intent intent = new Intent(getContext(), ReminderBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

        // 3 months duration in milliseconds
        long threeMonths = 90L * 24 * 60 * 60 * 1000; // 90 days

        // 7 days before the end of 3 months (milliseconds)
        long sevenDaysBefore = threeMonths - (7L * 24 * 60 * 60 * 1000); // 7 days before

        if (alarmManager != null) {
            // Schedule the first alarm for 3 months
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + threeMonths, pendingIntent);

            // Schedule an additional alarm for 7 days before the end of 3 months
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + sevenDaysBefore, pendingIntent);
        }
    }




   /* private void scheduleTrimesterReminder() {
        Intent intent = new Intent(getContext(), ReminderBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        long threeMonths = 90L * 24 * 60 * 60 * 1000;

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + threeMonths, threeMonths, pendingIntent);
        }
    } */
}

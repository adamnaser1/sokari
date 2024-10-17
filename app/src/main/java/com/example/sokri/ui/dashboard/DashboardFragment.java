package com.example.sokri.ui.dashboard;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sokri.GlucoseDatabaseHelper;
import com.example.sokri.databinding.FragmentDashboardBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private GlucoseDatabaseHelper dbHelper;
    private LineChart lineChart;
    private ArrayList<Measurement> allMeasurements;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize database helper and chart
        dbHelper = new GlucoseDatabaseHelper(getContext());
        lineChart = binding.lineChart;

        // Display data on startup
        displayData();

        // Set chart value selected listener
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int index = (int) e.getX(); // Get the selected point's index
                Measurement selectedMeasurement = allMeasurements.get(index); // Get the corresponding measurement

                // Display date and situation in the TextView
                String info = "Date: " + selectedMeasurement.getTime() + "\nSituation: " + selectedMeasurement.getSituation();
                binding.selectedPointInfo.setText(info);
            }

            @Override
            public void onNothingSelected() {
                // When nothing is selected, clear the text
                binding.selectedPointInfo.setText("Sélectionnez un point");
            }
        });

        // Handle filter date button
        ImageButton filterDateButton = binding.filterDateButton;
        filterDateButton.setOnClickListener(v -> showDatePicker());

        // Handle clear filter button
        ImageButton clearFilterButton = binding.clearFilterButton;
        clearFilterButton.setOnClickListener(v -> clearDateFilter());

        return root;
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                    filterMeasurementsByDate(selectedDate);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void filterMeasurementsByDate(String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM measurements WHERE time LIKE ?", new String[]{date + "%"});

        ArrayList<Measurement> measurements = new ArrayList<>();
        while (cursor.moveToNext()) {
            int glucose = cursor.getInt(1);
            String time = cursor.getString(2);
            String situation = cursor.getString(3);
            String insulinDose = cursor.getString(4);
            String notes = cursor.getString(5);

            measurements.add(new Measurement(glucose, time, situation, insulinDose, notes));
        }

        cursor.close();
        MeasurementAdapter adapter = new MeasurementAdapter(getContext(), measurements);
        binding.listView.setAdapter(adapter);

        displayStatistics(measurements); // Update statistics with filtered data
    }

    private void clearDateFilter() {
        // Clear the filter and display all data
        MeasurementAdapter adapter = new MeasurementAdapter(getContext(), allMeasurements);
        binding.listView.setAdapter(adapter);
        displayStatistics(allMeasurements);
    }

    private void displayData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM measurements", null);

        allMeasurements = new ArrayList<>();
        while (cursor.moveToNext()) {
            int glucose = cursor.getInt(1);
            String time = cursor.getString(2);
            String situation = cursor.getString(3);
            String insulinDose = cursor.getString(4);
            String notes = cursor.getString(5);

            allMeasurements.add(new Measurement(glucose, time, situation, insulinDose, notes));
        }

        cursor.close();

        MeasurementAdapter adapter = new MeasurementAdapter(getContext(), allMeasurements);
        binding.listView.setAdapter(adapter);

        displayStatistics(allMeasurements);
    }

    private void displayStatistics(ArrayList<Measurement> measurements) {
        ArrayList<Entry> entries = new ArrayList<>();
        int index = 0;

        for (Measurement measurement : measurements) {
            entries.add(new Entry(index, measurement.getGlucose()));
            index++;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Niveaux de glycémie");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        LineData lineData = new LineData(dataSet);

        lineChart.setData(lineData);
        lineChart.invalidate(); // Refresh the chart

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Calculate glucose statistics
        int normalCount = 0, lowCount = 0, highCount = 0;

        for (Measurement measurement : measurements) {
            int value = measurement.getGlucose();
            if (value >= 80 && value <= 140) normalCount++;
            else if (value < 80) lowCount++;
            else if (value > 190) highCount++;
        }

        binding.textStatistics.setText("Statistiques :\n" +
                "Normal : " + normalCount + "\n" +
                "Bas : " + lowCount + "\n" +
                "Élevé : " + highCount);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

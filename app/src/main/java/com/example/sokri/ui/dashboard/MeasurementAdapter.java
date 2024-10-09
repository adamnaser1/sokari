package com.example.sokri.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sokri.R;

import java.util.ArrayList;

public class MeasurementAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Measurement> measurements;

    public MeasurementAdapter(Context context, ArrayList<Measurement> measurements) {
        this.context = context;
        this.measurements = measurements;
    }

    @Override
    public int getCount() {
        return measurements.size();
    }

    @Override
    public Object getItem(int position) {
        return measurements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_measurement, parent, false);
        }

        Measurement measurement = measurements.get(position);

        TextView glucose = convertView.findViewById(R.id.glucose);
        TextView time = convertView.findViewById(R.id.time);
        TextView situation = convertView.findViewById(R.id.situation);
        TextView insulinDose = convertView.findViewById(R.id.insulin_dose);
        TextView notes = convertView.findViewById(R.id.notes);

        glucose.setText("Glycémie : " + measurement.getGlucose());
        time.setText("Temps : " + measurement.getTime());
        situation.setText("Situation : " + measurement.getSituation());
        insulinDose.setText("Insuline : " + measurement.getInsulinDose());
        notes.setText("Notes : " + measurement.getNotes());



        return convertView;
    }
}

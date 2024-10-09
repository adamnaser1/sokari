package com.example.sokri.ui.dashboard;

public class Measurement {
    private int glucose;
    private String time;
    private String situation;
    private String insulinDose;
    private String notes;


    public Measurement(int glucose, String time, String situation, String insulinDose, String notes) {
        this.glucose = glucose;
        this.time = time;
        this.situation = situation;
        this.insulinDose = insulinDose;
        this.notes = notes;
    }

    public int getGlucose() { return glucose; }
    public String getTime() { return time; }
    public String getSituation() { return situation; }
    public String getInsulinDose() { return insulinDose; }
    public String getNotes() { return notes; }

}

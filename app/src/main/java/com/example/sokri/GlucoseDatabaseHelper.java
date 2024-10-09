package com.example.sokri;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GlucoseDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "glucose_data.db";
    private static final int DATABASE_VERSION = 2;

    public GlucoseDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE measurements (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "glucose_value INTEGER, " +
                "time TEXT, " +
                "situation TEXT, " +
                "insulin_dose TEXT, " +
                "notes TEXT)"); // Ajout de la colonne notes
        db.execSQL("CREATE TABLE quarterly_measurements (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date TEXT, " +
                "percentage TEXT, " +
                "next_appointment TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // If upgrading from version 1, add the quarterly_measurements table
            db.execSQL("CREATE TABLE quarterly_measurements (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "date TEXT, " +
                    "percentage TEXT)");
        }
    }
}

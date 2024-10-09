package com.example.sokri.ui.trimonthly;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TrimesterViewModel extends ViewModel {
    private final MutableLiveData<String> selectedDate;
    private final MutableLiveData<String> glucosePercentage;

    public TrimesterViewModel() {
        selectedDate = new MutableLiveData<>();
        glucosePercentage = new MutableLiveData<>();
    }

    // Getter for selectedDate
    public LiveData<String> getSelectedDate() {
        return selectedDate;
    }

    // Setter for selectedDate
    public void setSelectedDate(String date) {
        selectedDate.setValue(date);
    }

    // Getter for glucosePercentage
    public LiveData<String> getGlucosePercentage() {
        return glucosePercentage;
    }

    // Setter for glucosePercentage
    public void setGlucosePercentage(String percentage) {
        glucosePercentage.setValue(percentage);
    }
}

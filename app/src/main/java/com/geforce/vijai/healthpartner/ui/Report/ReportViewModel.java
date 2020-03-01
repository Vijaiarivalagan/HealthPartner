package com.geforce.vijai.healthpartner.ui.Report;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReportViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ReportViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is tools fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
package com.geforce.vijai.healthpartner;

import java.util.Date;

public class DiabetesReportModel {
    long date;
    int value;
    String type;

    public DiabetesReportModel(){}
    public DiabetesReportModel(long date, int value, String type) {
        this.date = date;
        this.value = value;
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}


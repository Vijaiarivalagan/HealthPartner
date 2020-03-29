package com.geforce.vijai.healthpartner;

public class ModelreportSteps {
     long date;
     int steps;

     ModelreportSteps() {
    }

     ModelreportSteps(long date, int steps) {
        this.date = date;
        this.steps = steps;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}

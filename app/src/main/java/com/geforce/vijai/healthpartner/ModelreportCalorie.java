package com.geforce.vijai.healthpartner;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ModelreportCalorie {

    long date;
    int calorie;
    ModelreportCalorie(){}
    ModelreportCalorie(long date, int calorie){
        this.date=date;
        this.calorie=calorie;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }
}

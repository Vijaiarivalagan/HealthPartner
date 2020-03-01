package com.geforce.vijai.healthpartner.ui.home;

import java.util.ArrayList;

public class VerticleModel {
    String SessionTitle;
    ArrayList<HorizontalModel> arrayList;

    public String getSessionTitle() {
        return SessionTitle;
    }

    public void setSessionTitle(String sessionTitle) {
        SessionTitle = sessionTitle;
    }

    public ArrayList<HorizontalModel> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<HorizontalModel> arrayList) {
        this.arrayList = arrayList;
    }
}

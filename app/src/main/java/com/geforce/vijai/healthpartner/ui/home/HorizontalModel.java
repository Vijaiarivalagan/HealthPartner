package com.geforce.vijai.healthpartner.ui.home;

public class HorizontalModel {
    int calorie;
    String food;
    String food_session;



    HorizontalModel(){}
    public String getFood_session() {
        return food_session;
    }

    public void setFood_session(String food_session) {
        this.food_session = food_session;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }
}

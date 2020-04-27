package com.geforce.vijai.healthpartner;

public class ModelEnergyCount {
    int burn;
    String work;

    public ModelEnergyCount() {
    }

    public ModelEnergyCount(int burn, String work) {
        this.burn = burn;
        this.work = work;
    }

    public int getBurn() {
        return burn;
    }

    public void setBurn(int burn) {
        this.burn = burn;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }
}

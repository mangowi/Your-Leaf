package com.kaloyanov.ivan.yourleaf.models;

import com.kaloyanov.ivan.yourleaf.models.interfaces.BaseSensor;
/*
 * @author ivan.kaloyanov
 */
public class WateringDataModel implements BaseSensor {

    private double value;
    private double optimal;

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public void setValue(double value) {
        this.value = value;
    }

    public double getOptimal() {
        return optimal;
    }

    public void setOptimal(double optimal) {
        this.optimal = optimal;
    }
}

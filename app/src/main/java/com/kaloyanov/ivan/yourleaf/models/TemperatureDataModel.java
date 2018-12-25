package com.kaloyanov.ivan.yourleaf.models;

import com.kaloyanov.ivan.yourleaf.models.interfaces.BaseSensor;
/*
 * @author ivan.kaloyanov
 */
public class TemperatureDataModel implements BaseSensor {

    private double value;
    private boolean isHeating;

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public void setValue(double value) {
        this.value = value;
    }

    public boolean isHeating() {
        return isHeating;
    }

    public void setHeating(boolean heating) {
        isHeating = heating;
    }
}

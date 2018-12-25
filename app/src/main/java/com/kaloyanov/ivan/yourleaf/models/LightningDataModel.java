package com.kaloyanov.ivan.yourleaf.models;

import com.kaloyanov.ivan.yourleaf.models.interfaces.BaseSensor;
/*
 * @author ivan.kaloyanov
 */
public class LightningDataModel implements BaseSensor {

    private boolean state;
    private double value;

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}

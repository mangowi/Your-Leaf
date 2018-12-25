package com.kaloyanov.ivan.yourleaf.activities.temperature;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kaloyanov.ivan.yourleaf.R;
import com.kaloyanov.ivan.yourleaf.controllers.ArduinoController;
import com.kaloyanov.ivan.yourleaf.models.TemperatureDataModel;
import com.kaloyanov.ivan.yourleaf.requests.HttpRequest;
import com.kaloyanov.ivan.yourleaf.responses.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TemperatureFragment extends Fragment {

    // Inject the Text components from the view
    @Nullable
    public @BindView(R.id.temperatureCurrentData) TextView currentDataText;

    @Nullable
    public @BindView(R.id.temperatureStateButton)
    Button temperatureButton;

    private static final int SENSOR_UPDATE_TIME_SEC = 10;
    private static final String TEMPERATURE_ROUTE = "/temperature";

    private double currentValue;
    private boolean isHeating;
    private ScheduledExecutorService executorService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_temperature, container, false);

        executorService = Executors.newSingleThreadScheduledExecutor();
        ButterKnife.bind(this, rootView);
        this.updateData();

        temperatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onManageTheHeat();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    public TemperatureFragment() {
    }

    // Turn the heating on and off when the heat button is pressed
    private void onManageTheHeat() throws IOException, JSONException {
        HttpRequest manageTheHeatRequest = new HttpRequest(TEMPERATURE_ROUTE,null,getResources().getString(R.string.POST));
        HttpResponse response = ArduinoController.instance().sendHttpRequest(manageTheHeatRequest);
        parseResponse(response);
        updateModel();
    }

    /*
     * Make a HTTP request to get the data
     * @throws IOException if the request is bad
     * @return HttpResponse with status and data
     */
    private HttpResponse requestTemperatureSensorData() throws IOException {
        HttpRequest temperatureSensorRequest = new HttpRequest(TEMPERATURE_ROUTE,null,getResources().getString(R.string.GET));
        HttpResponse response = ArduinoController.instance().sendHttpRequest(temperatureSensorRequest);
        return response;
    }

    /*
     *  Parse HttpResponse into TemperatureDataModel
     *  @param HttpResponse response, the object to get parse
     *  @return TemperatureDataModel with the response data
     */
    private TemperatureDataModel parseTemperatureData(HttpResponse response) throws JSONException {
        final JSONObject jsonParser = new JSONObject(response.getData());
        TemperatureDataModel result = new TemperatureDataModel();
        result.setValue(jsonParser.getDouble(getResources().getString(R.string.value)));
        result.setHeating(jsonParser.getBoolean(getResources().getString(R.string.state)));
        return result;
    }

    // Start a thread to obtain the temperature data every 10 secs
    private void updateData(){
        Runnable task = new Runnable() {
            public void run() {
                try {
                    obtainSensorData();
                    updateModel();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        };
        executorService.scheduleAtFixedRate(task, 0,SENSOR_UPDATE_TIME_SEC, TimeUnit.SECONDS);
    }

    // Get the data and set it to the fields
    private void obtainSensorData() throws IOException, JSONException {
        HttpResponse response = requestTemperatureSensorData();
        parseResponse(response);
    }

    // Extract the data from the response and set it to the fields
    private void parseResponse(HttpResponse response) throws JSONException {
        TemperatureDataModel model = parseTemperatureData(response);
        this.setCurrentValue(model.getValue());
        this.setHeating(model.isHeating());
    }

    // Update the view
    private void updateModel(){
        currentDataText.setText(String.valueOf(this.getCurrentValue()) + " C");
        temperatureButton.setText(this.isHeating() ?
                getResources().getString(R.string.on) : getResources().getString(R.string.off));
    }

    private double getCurrentValue() {
        return currentValue;
    }

    private void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    private boolean isHeating() {
        return isHeating;
    }

    private void setHeating(boolean heating) {
        isHeating = heating;
    }
}
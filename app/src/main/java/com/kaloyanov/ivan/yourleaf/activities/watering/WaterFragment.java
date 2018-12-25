package com.kaloyanov.ivan.yourleaf.activities.watering;

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
import com.kaloyanov.ivan.yourleaf.fragments.ActionFragment;
import com.kaloyanov.ivan.yourleaf.models.WateringDataModel;
import com.kaloyanov.ivan.yourleaf.requests.HttpRequest;
import com.kaloyanov.ivan.yourleaf.responses.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class WaterFragment extends Fragment {

    private static final int SENSOR_UPDATE_TIME_SEC = 10;
    private static final String WATERING_ROUTE = "/watering";

    // Guessing values
    private static final Integer WATER_AMOUNT_LITTLE = 5;
    private static final Integer WATER_AMOUNT_MEDIUM = 10;
    private static final Integer WATER_AMOUNT_EXTRA = 15;

    private double optimalValue;
    private double currentValue;
    private ScheduledExecutorService executorService;

    // Inject the Text components from the view
    @Nullable
    public @BindView(R.id.wateringOptimalData)
    TextView optimalDataText;

    @Nullable
    public @BindView(R.id.wateringCurrentData)
    TextView currentDataText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_water, container, false);
        executorService = Executors.newSingleThreadScheduledExecutor();
        this.updateData();

        Button littleWaterButton = (Button) rootView.findViewById(R.id.littleWaterButton);
        littleWaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onWateringLittle();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button mediumWaterButton = (Button) rootView.findViewById(R.id.mediumWaterButton);
        mediumWaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onWateringMedium();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button extraWaterButton = (Button) rootView.findViewById(R.id.extraWaterButton);
        extraWaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onWateringExtra();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    public WaterFragment() {}

    /*
     * Make a HTTP request to get the data
     * @throws IOException if the request is bad
     * @return HttpResponse with status and data
     */
    private HttpResponse requestWateringSensorData() throws IOException {
        HttpRequest wateringSensorRequest = new HttpRequest(WATERING_ROUTE,null,getResources().getString(R.string.GET));
        HttpResponse response = ArduinoController.instance().sendHttpRequest(wateringSensorRequest);
        return response;
    }

    /*
     * Do a watering request to watter the plant
     * @param Integer amount the amount of time to sink
     * @throws IOException if the request fails
     */
    private void watering(Integer amount) throws IOException {
        HttpRequest wateringSensorRequest = new HttpRequest(WATERING_ROUTE,
                String.format("{amount:%s}", amount.toString()),getResources().getString(R.string.GET));
        HttpResponse response = ArduinoController.instance().sendHttpRequest(wateringSensorRequest);
        if(response.getStatus() == 200){
            this.showDialogWithMessage(getResources().getString(R.string.wateringDone));
        }
        else{
            this.showDialogWithMessage(getResources().getString(R.string.wateringFailed));
        }
    }

    /*
     *  Parse HttpResponse into WateringDataModel
     *  @param HttpResponse response, the object to get parse
     *  @return WateringDataModel with the response data
     */
    private WateringDataModel parseWateringData(HttpResponse response) throws JSONException {
        final JSONObject jsonParser = new JSONObject(response.getData());
        WateringDataModel result = new WateringDataModel();
        result.setValue(jsonParser.getDouble(getResources().getString(R.string.value)));
        result.setOptimal(jsonParser.getDouble(getResources().getString(R.string.optimal)));
        return result;
    }

    /*
     *  Show a dialog message
     *  @param String message, the message to show
     */
    private void showDialogWithMessage(String message){
        ActionFragment saveFragment = ActionFragment.newInstance(message);
        saveFragment.show(getFragmentManager(),message);
    }

    private void updateModel(){
        optimalDataText.setText(String.valueOf(getOptimalValue()));
        currentDataText.setText(String.valueOf(getCurrentValue()));
    }

    // Start a thread to obtain the sensor data every 10 secs
    private void updateData(){
        Runnable task = new Runnable() {
            public void run() {
                try {
                    obtainSensorData();
                    updateModel();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        executorService.scheduleAtFixedRate(task, 0,SENSOR_UPDATE_TIME_SEC, TimeUnit.SECONDS);
    }

    // Get and set the data from the board
    private void obtainSensorData() throws IOException, JSONException {
        HttpResponse response = requestWateringSensorData();
        WateringDataModel model = parseWateringData(response);
        this.setCurrentValue(model.getValue());
        this.setOptimalValue(model.getOptimal());
    }

    // Do watering with some pre-defined values
    private void onWateringLittle() throws IOException {
        this.watering(WATER_AMOUNT_LITTLE);
    }

    private void onWateringMedium() throws IOException {
        this.watering(WATER_AMOUNT_MEDIUM);
    }

    private void onWateringExtra() throws IOException {
        this.watering(WATER_AMOUNT_EXTRA);
    }

    public double getOptimalValue() {
        return optimalValue;
    }

    public void setOptimalValue(double optimalValue) {
        this.optimalValue = optimalValue;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

}
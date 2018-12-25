package com.kaloyanov.ivan.yourleaf.activities.lightning;

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
import com.kaloyanov.ivan.yourleaf.models.LightningDataModel;
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

public class LightFragment extends Fragment {

    private static final int SENSOR_UPDATE_TIME_SEC = 5;
    private static final String LIGHTNING_ROUTE = "/lightning";

    private ScheduledExecutorService executorService;
    private boolean lightningState;
    private double currentValue;

    @Nullable
    public @BindView(R.id.lightningStateButton)
    Button lightningButton;

    @Nullable
    public @BindView(R.id.lightningCurrentData)
    TextView currentDataText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_light, container, false);
        ButterKnife.bind(this,rootView);
        executorService = Executors.newSingleThreadScheduledExecutor();
        this.updateData();

        lightningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onManageTheLights();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }

    /* Make a HTTP request to get the lightning data
     * @return HttpResponse with status code and data
     */
    private HttpResponse requestLightningSensorData() throws IOException {
        HttpRequest lightningSensorRequest = new HttpRequest(LIGHTNING_ROUTE,null,getResources().getString(R.string.GET));
        HttpResponse response = ArduinoController.instance().sendHttpRequest(lightningSensorRequest);
        return response;
    }

    /*
     * Parse the HttpResponse data into LightningDataModel
     * @throws JSONException if the parsing fails
     * @return LightningDataModel with response's data
     */
    private LightningDataModel parseResponse(HttpResponse response) throws JSONException {
        final JSONObject jsonParser = new JSONObject(response.getData());
        LightningDataModel result = new LightningDataModel();
        result.setValue( jsonParser.getDouble(getResources().getString(R.string.value)));
        result.setState( jsonParser.getBoolean(getResources().getString(R.string.state)));
        return result;
    }

    // Turn the lights on and off when you click on the button
    private void onManageTheLights() throws IOException, JSONException {
        HttpRequest manageLightningRequest = new HttpRequest(LIGHTNING_ROUTE,null,getResources().getString(R.string.POST));
        HttpResponse response = ArduinoController.instance().sendHttpRequest(manageLightningRequest);

        if(response.getStatus() == 200) {
            LightningDataModel data = parseResponse(response);
            this.setupData(data);
            this.updateModel();
        }
        else{
            ActionFragment saveFragment = ActionFragment.newInstance(getResources().getString(R.string.operationFailed));
            saveFragment.show(getFragmentManager(),"manageLights");
        }
    }

    /*
     *  Start a thread to update the lightning data every 5 secs
     *  if your board supports websockets feel free to change it
     */
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

    // Request the data, parse it and set it
    private void obtainSensorData() throws IOException, JSONException {
        HttpResponse request = requestLightningSensorData();
        LightningDataModel data = parseResponse(request);
        setupData(data);
    }

    private void setupData(LightningDataModel data){
        setCurrentValue(data.getValue());
        setLightningState(data.getState());
    }

    // Update the lightning state text
    private void updateModel(){
        this.lightningButton.setText( this.getLightningState() ?
                getResources().getString(R.string.on) : getResources().getString(R.string.off));
        this.currentDataText.setText(String.valueOf(this.getCurrentValue()) + "%");
    }

    public boolean getLightningState() {
        return lightningState;
    }

    public void setLightningState(boolean lightningState) {
        this.lightningState = lightningState;
    }

    public double getCurrentValue() { return currentValue; }

    public void setCurrentValue(double currentValue) { this.currentValue = currentValue; }

    public LightFragment() {
    }
}
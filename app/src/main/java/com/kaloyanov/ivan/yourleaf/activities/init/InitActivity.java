package com.kaloyanov.ivan.yourleaf.activities.init;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.kaloyanov.ivan.yourleaf.activities.main.MainActivity;
import com.kaloyanov.ivan.yourleaf.activities.welcome.WelcomeActivity;
import com.kaloyanov.ivan.yourleaf.controllers.ArduinoController;
import com.kaloyanov.ivan.yourleaf.R;
import com.kaloyanov.ivan.yourleaf.exceptions.NotAuthorizedException;
import com.kaloyanov.ivan.yourleaf.fragments.ActionFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
/*
 * @author ivan.kaloyanov
 */
public class InitActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private SharedPreferences.Editor preferenceEditor;

    // Inject View's text component for the address
    @Nullable
    public @BindView(R.id.address) EditText addressEditTex;

    // Inject View's text component for the password
    @Nullable
    public @BindView(R.id.password) EditText passwordEditTex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ButterKnife.setDebug(true);
        super.onCreate(savedInstanceState);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(InitActivity.this);

        // Allow http request on the main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // goto Home Activity if you've been authenticated before
        String address = preferences.getString(String.valueOf(R.string.address), null);
        String password = preferences.getString(String.valueOf(R.string.password), null);

        if(address != null && password != null){
            try {
                boolean isAuthorized = ArduinoController.instance().login(address, password);
                if(isAuthorized) {
                    String systemName = preferences.getString(String.valueOf(R.string.systemName), null);

                    if (systemName != null) {
                        Intent intent = new Intent(InitActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(InitActivity.this,
                                WelcomeActivity.class);
                        startActivity(intent);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                setContentView(R.layout.activity_init);
            }
        }
        else {
            setContentView(R.layout.activity_init);
        }
        ButterKnife.bind(this);
    }

    /*
    * Try to authenticate with the passed credentials and if
    * the status is okay goto Welcome Activity
    */
    @Optional
    @OnClick({R.id.address, R.id.password})
    public void onConnect(View view) {
        String address = addressEditTex.getText().toString();
        String password = passwordEditTex.getText().toString();

        try {
           boolean isAuthorized = ArduinoController.instance().login(address, password);

            if(isAuthorized){
                this.preferenceEditor = preferences.edit();
                preferenceEditor.putString(String.valueOf(R.string.address), address);
                preferenceEditor.putString(String.valueOf(R.string.password), password);
                preferenceEditor.commit();

                Intent intent = new Intent(InitActivity.this,
                        WelcomeActivity.class);
                startActivity(intent);
            }
            else{
                throw new NotAuthorizedException("Invalid credentials");
            }
        }catch(IOException | NotAuthorizedException e){
            e.printStackTrace();
            if(!( address.isEmpty() && password.isEmpty())) {
                ActionFragment saveFragment = ActionFragment.newInstance(getResources().getString(R.string.unableToAuthenticate));
                saveFragment.show(getSupportFragmentManager(), "Not Authorized");
            }
        }
    }

}

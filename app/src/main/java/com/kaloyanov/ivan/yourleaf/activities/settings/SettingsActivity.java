package com.kaloyanov.ivan.yourleaf.activities.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kaloyanov.ivan.yourleaf.R;
import com.kaloyanov.ivan.yourleaf.activities.main.MainActivity;
import com.kaloyanov.ivan.yourleaf.controllers.ArduinoController;
import com.kaloyanov.ivan.yourleaf.exceptions.NotAuthorizedException;
import com.kaloyanov.ivan.yourleaf.fragments.ActionFragment;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
/*
 * @author ivan.kaloyanov
 */
public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private SharedPreferences.Editor preferenceEditor;

    // Inject the Text components from the view
    @Nullable
    public @BindView(R.id.systemName) EditText systemEditTex;

    @Nullable
    public @BindView(R.id.address) EditText addressEditTex;

    @Nullable
    public @BindView(R.id.password) EditText passwordEditTex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.preferenceEditor = preferences.edit();
        String prefAddress = preferences.getString(String.valueOf(R.string.address), "");
        String prefPassword = preferences.getString(String.valueOf(R.string.password), "");
        String prefSystemName = preferences.getString(String.valueOf(R.string.systemName), "");

        addressEditTex.setText(prefAddress);
        passwordEditTex.setText(prefPassword);
        systemEditTex.setText(prefSystemName);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button saveSettingsButton = (Button) findViewById(R.id.saveSettingsButton);
        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSavePressed();
            }
        });
    }

    // Save the changes if there is one
    private void onSavePressed() {
        this.preferenceEditor = preferences.edit();
        String prefAddress = preferences.getString(String.valueOf(R.string.address), null);
        String prefPassword = preferences.getString(String.valueOf(R.string.password), null);
        String prefSystemName = preferences.getString(String.valueOf(R.string.systemName), null);

        String fieldAddress = addressEditTex.getText().toString();
        String fieldPassword = passwordEditTex.getText().toString();
        String fieldSystemName = systemEditTex.getText().toString();

        if (!prefAddress.equals(fieldAddress) || !prefPassword.equals(fieldPassword) || !prefSystemName.equals(fieldSystemName)) {
            try {
                boolean isAuthorized = ArduinoController.instance().login(fieldAddress, fieldPassword);
                if (isAuthorized) {
                    preferenceEditor.putString(String.valueOf(R.string.address), fieldAddress);
                    preferenceEditor.putString(String.valueOf(R.string.password), fieldPassword);
                    preferenceEditor.putString(String.valueOf(R.string.systemName), fieldSystemName);
                }
                else{
                    throw new NotAuthorizedException("Invalid credentials");
                }
            } catch (IOException | NotAuthorizedException e) {
                Log.e(e.getLocalizedMessage(), e.getMessage());
                ActionFragment saveFragment = ActionFragment.newInstance(getResources().getString(R.string.unableToAuthenticate));
                saveFragment.show(getSupportFragmentManager(),"unableToConnect");
                return;
            }

            ActionFragment saveFragment = ActionFragment.newInstance(getResources().getString(R.string.settingsSaved));
            saveFragment.show(getSupportFragmentManager(),"save");
            preferenceEditor.commit();
        }
    }

    // Go back to Home Activity when back button is pressed
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingsActivity.this,
                MainActivity.class);
        startActivity(intent);
    }
}

package com.kaloyanov.ivan.yourleaf.activities.welcome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.kaloyanov.ivan.yourleaf.R;
import com.kaloyanov.ivan.yourleaf.activities.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
/*
 * @author ivan.kaloyanov
 */
public class WelcomeActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private SharedPreferences.Editor preferenceEditor;

    // Inject the Text component
    @Nullable
    public @BindView(R.id.systemNameInput) EditText systemNameEditTex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ButterKnife.setDebug(true);
        super.onCreate(savedInstanceState);

        // Goto Home Activity if there is a set System Name
        this.preferences = PreferenceManager.getDefaultSharedPreferences(WelcomeActivity.this);
        String systemName = preferences.getString(String.valueOf(R.string.systemName), null);
        if(systemName != null && !systemName.isEmpty()){
            Intent intent = new Intent(WelcomeActivity.this,
                        MainActivity.class);
            startActivity(intent);
        }
        else{
            setContentView(R.layout.activity_welcome);
        }
        ButterKnife.bind(this);
    }

    // Set the System Name and goto Home Activity
    @Optional
    @OnClick(R.id.systemNameInput)
    public void onCreateSystem(View view){
        String systemName = systemNameEditTex.getText().toString();
        if(systemName != null && !systemName.isEmpty()) {

            this.preferenceEditor = preferences.edit();
            this.preferenceEditor.putString(String.valueOf(R.string.systemName), systemName);
            this.preferenceEditor.commit();

            //Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
            //startActivity(intent);

            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    // Do nothing when back button is pressed
    @Override
    public void onBackPressed() { }
}

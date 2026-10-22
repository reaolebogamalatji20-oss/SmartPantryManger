package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS = "smart_pantry_prefs";
    public static final String KEY_HIGHLIGHT_EXPIRING = "highlight_expiring";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        SwitchMaterial sw = findViewById(R.id.switchExpiringAlerts);
        sw.setChecked(prefs.getBoolean(KEY_HIGHLIGHT_EXPIRING, true));

        // Save immediately whenever the switch is toggled
        sw.setOnCheckedChangeListener((button, isChecked) ->
                prefs.edit().putBoolean(KEY_HIGHLIGHT_EXPIRING, isChecked).apply());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
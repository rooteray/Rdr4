package com.rtry.rdr4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Switch manga_mode = (Switch) findViewById(R.id.manga_mode_switch);
        manga_mode.setChecked(MainActivity.getMangaMode());
        manga_mode.setOnCheckedChangeListener((buttonView, isChecked) -> MainActivity.setManga_mode(isChecked));
    }
}


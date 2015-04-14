package com.lob.quicktranslate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences settings;
    SharedPreferences.Editor editor;
    int toastDuration;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(Color.DKGRAY);

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String toastDurationS = settings.getString("toastDuration", "2");

        if (toastDurationS.equals("1"))
            this.toastDuration = 3000;
        else if (toastDurationS.equals("2"))
            this.toastDuration = 4000;
        else if (toastDurationS.equals("3"))
            this.toastDuration = 5000;

        editor = settings.edit();
        editor.putInt("toastDurationMs", toastDuration);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
        if (key.equals("mustShowNotification")) {
            stopService(new Intent(getApplicationContext(), CopyService.class));
            startService(new Intent(getApplicationContext(), CopyService.class));
        }
    }

}

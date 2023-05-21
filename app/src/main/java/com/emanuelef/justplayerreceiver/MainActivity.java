package com.emanuelef.justplayerreceiver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        Intent intent = new Intent(getApplicationContext(), MainService.class);
        ContextCompat.startForegroundService(this, intent);
        finish();
    }
}

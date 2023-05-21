package com.emanuelef.justplayerreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent startIntent) {
        String action = startIntent.getAction();
        Log.d(TAG, "onReceive: " + action);

        if(!action.equals(Intent.ACTION_BOOT_COMPLETED) && !action.equals("android.intent.action.QUICKBOOT_POWERON")) {
            Log.w(TAG, "Unexpected action: " + action);
            return;
        }

        Intent intent = new Intent(context, MainService.class);
        ContextCompat.startForegroundService(context, intent);
    }
}

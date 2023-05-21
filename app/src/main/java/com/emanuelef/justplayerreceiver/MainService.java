package com.emanuelef.justplayerreceiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;

public class MainService extends Service {
    private static final String TAG = "MainService";
    private static final String NOTIFY_CHAN_MAIN = "main";
    private static final int HTTP_PORT = 8028;

    private String mRegisteredMdnsName;

    @Override
    public void onCreate() {
        super.onCreate();

        startForeground(1, getNotification());
        registerMdnsService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mRegisteredMdnsName != null)
            Toast.makeText(MainService.this, mRegisteredMdnsName, Toast.LENGTH_LONG).show();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String getServiceName() {
        return Build.MODEL + "-just-player";
    }

    private Notification getNotification() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel chan = new NotificationChannel(NOTIFY_CHAN_MAIN,
                    "Main notification channel", NotificationManager.IMPORTANCE_LOW); // low: no sound
            chan.setShowBadge(false);
            nm.createNotificationChannel(chan);
        }

        return new NotificationCompat.Builder(this, NOTIFY_CHAN_MAIN)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setAutoCancel(false)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentTitle("HTTP receiver running")
                .setContentText(String.format("MDNS service name: %s", getServiceName()))
                .build();
    }

    private void registerMdnsService() {
        NsdServiceInfo service = new NsdServiceInfo();

        service.setServiceName(getServiceName());
        service.setServiceType("_http._tcp");
        service.setPort(HTTP_PORT);

        String mdns_name = service.getServiceName() + ".local";

        NsdManager.RegistrationListener listener = new NsdManager.RegistrationListener() {
            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                Log.i(TAG, "Service registered");
                mRegisteredMdnsName = mdns_name;
                Toast.makeText(MainService.this, mdns_name, Toast.LENGTH_LONG).show();

                HttpReceiver receiver = new HttpReceiver(MainService.this, HTTP_PORT);
                try {
                    receiver.start();
                    Log.i(TAG, "HTTP server listening at " + HTTP_PORT);
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                    stopSelf();
                }
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errCode) {
                Log.e(TAG, "Service register failed: " + errCode);
                stopSelf();
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errCode) {
                Log.e(TAG, "Service unregister failed: " + errCode);
                stopSelf();
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                Log.i(TAG, "Service unregistered");
                stopSelf();
            }
        };

        NsdManager nsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);

        Log.i(TAG, "Registering MDNS service " + mdns_name);
        nsdManager.registerService(service, NsdManager.PROTOCOL_DNS_SD, listener);
    }
}

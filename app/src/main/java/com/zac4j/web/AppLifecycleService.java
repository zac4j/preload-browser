package com.zac4j.web;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service for detecting App lifecycle change.
 * Created by Zaccc on 2018/1/23.
 */

public class AppLifecycleService extends Service {

    private static final String TAG = AppLifecycleService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Logger.d(TAG, "Service Destroyed");
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Logger.e(TAG, "Time to destroy WebView");
        stopSelf();
    }
}

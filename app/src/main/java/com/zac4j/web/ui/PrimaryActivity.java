package com.zac4j.web.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.zac4j.web.AppLifecycleService;
import com.zac4j.web.Logger;
import com.zac4j.web.R;
import com.zac4j.web.Utils;
import com.zac4j.web.browser.BrowserDialogManager;

public class PrimaryActivity extends AppCompatActivity {

    private static final String TAG = PrimaryActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Start service to detect app lifecycle.
        startService(new Intent(getBaseContext(), AppLifecycleService.class));
    }

    @Override
    protected void onStart() {
        Logger.d(TAG, "onStart");
        super.onStart();
        preload(Utils.provideUrl());
    }

    @Override
    protected void onDestroy() {
        Logger.d(TAG, "onDestroy");
        super.onDestroy();
    }

    public void getPacket(View view) {
        startActivity(new Intent(PrimaryActivity.this, SecondaryActivity.class));
    }

    private void preload(String url) {
        Logger.d(TAG, "Preload url in PrimaryActivity and will display in SecondaryActivity in a DialogFragment");
        // Step one: get BrowserManager instance
        BrowserDialogManager browserManager =  BrowserDialogManager.getInstance(getApplicationContext());
        // Step two: set url to preload data
        browserManager.preloadUrl(url);
        // Step three: set WebView instance settings, you can modify it urself by invoke BrowserManager.getWebView.
        browserManager.setupWebViewWithDefaults();
    }
}

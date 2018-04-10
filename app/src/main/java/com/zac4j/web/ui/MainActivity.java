package com.zac4j.web.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import com.zac4j.web.AppLifecycleService;
import com.zac4j.web.Logger;
import com.zac4j.web.R;
import com.zac4j.web.browser.BrowserDialogManager;
import com.zac4j.web.browser.Scheme;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

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
        byte[] buffer = Base64.decode(Scheme.URL, Base64.DEFAULT);
        try {
            String url = new String(buffer, "UTF-8");
            preload(url);
        } catch (UnsupportedEncodingException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        Logger.d(TAG, "onDestroy");
        super.onDestroy();
    }

    public void getPacket(View view) {
        startActivity(new Intent(MainActivity.this, SecondaryActivity.class));
    }

    private void preload(String url) {
        Logger.d(TAG, "Preload url in MainActivity and will display in SecondaryActivity in a DialogFragment");
        // Step one: get BrowserManager instance
        BrowserDialogManager browserManager =  BrowserDialogManager.getInstance(getApplicationContext());
        // Step two: set url to preload data
        browserManager.preloadUrl(url);
        // Step three: set WebView instance settings, you can modify it urself by invoke BrowserManager.getWebView.
        browserManager.setupWebViewWithDefaults();
    }
}

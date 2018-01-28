package com.zac4j.web.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.zac4j.web.AppLifecycleService;
import com.zac4j.web.Logger;
import com.zac4j.web.R;
import com.zac4j.web.browser.Browser;
import com.zac4j.web.browser.BrowserManager;
import com.zac4j.web.router.UrlRouter;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();
  private BrowserManager mBrowserManager;

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

    preload();
  }

  @Override
  protected void onDestroy() {
    Logger.d(TAG, "onDestroy");
    super.onDestroy();
  }

  public void getPacket(View view) {
    startActivity(new Intent(MainActivity.this, SecondaryActivity.class));
  }

  private void preload() {
    Logger.d(TAG, "Preload url in Main Activity");
    // Step one: get BrowserManager instance
    mBrowserManager = BrowserManager.getInstance(getApplicationContext());
    // Step two: set url to preload data
    mBrowserManager.preloadUrl(Browser.URL);
    // Step three: add intercept scheme in the WebViewClient::shouldOverrideUrlLoading url route specification.
    mBrowserManager.addUrlRouter(new UrlRouter() {
      @Override
      public boolean route(String scheme) {

        if (TextUtils.isEmpty(scheme)) {
          return false;
        }

        if (scheme.contains(Browser.OPEN_RED_PACKET)) {
          Toast.makeText(MainActivity.this, "Nice, You open this RadPacket!", Toast.LENGTH_SHORT)
              .show();
          return true;
        } else if (scheme.contains(Browser.CLOSE_RED_PACKET)) {
          mBrowserManager.closeDialog();
          return true;
        }

        return false;
      }
    });
    // Step four: set WebView instance settings, you can modify it urself by invoke BrowserManager.getWebView.
    mBrowserManager.setupWebViewWithDefaults();
  }

}

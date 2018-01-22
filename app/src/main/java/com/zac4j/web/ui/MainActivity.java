package com.zac4j.web.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.zac4j.web.Logger;
import com.zac4j.web.R;
import com.zac4j.web.browser.Browser;
import com.zac4j.web.browser.BrowserManager;
import com.zac4j.web.router.UrlRouter;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Logger.d(TAG, "Preload url in Main Activity");
    final BrowserManager browserManager = BrowserManager.getInstance(getApplicationContext());
    browserManager.preloadUrl(Browser.URL);
    browserManager.addUrlRouter(new UrlRouter() {
      @Override
      public boolean route(String scheme) {

        if (TextUtils.isEmpty(scheme)) {
          return false;
        }

        if (scheme.contains("gtjayyz://jumpfunc")) {
          Toast.makeText(MainActivity.this, "You opened the RadPacket!", Toast.LENGTH_SHORT).show();
          return true;
        } else if (scheme.contains("gtjanormal://activityClose")) {
          browserManager.closeDialog();
          return true;
        }

        return false;
      }
    });
    browserManager.setupWebViewWithDefaults();
  }

  public void getPacket(View view) {
    startActivity(new Intent(MainActivity.this, SecondaryActivity.class));
  }
}

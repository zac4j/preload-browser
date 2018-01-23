package com.zac4j.web.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.zac4j.web.Logger;
import com.zac4j.web.R;
import com.zac4j.web.browser.Browser;
import com.zac4j.web.browser.BrowserManager;

public class SecondaryActivity extends AppCompatActivity {

  private static final String TAG = SecondaryActivity.class.getSimpleName();
  private BrowserManager mBrowserManager;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState,
      @Nullable PersistableBundle persistentState) {
    super.onCreate(savedInstanceState, persistentState);
    setContentView(R.layout.activity_secondary);
  }

  @Override
  protected void onStart() {
    super.onStart();
    Logger.d(TAG, "onStart");

    mBrowserManager = BrowserManager.getInstance(getApplicationContext());

    // Verify if browser manager has preload complete.
    if (mBrowserManager.hasPreloadComplete()) {
      Logger.d(TAG, "Web page has preload complete");
    } else {
      Logger.d(TAG, "Web page haven't load yet");
      mBrowserManager.loadUrl(Browser.URL);
    }

    mBrowserManager.showDialog(getSupportFragmentManager(), RedPacketDialogFragment.class);
  }
}

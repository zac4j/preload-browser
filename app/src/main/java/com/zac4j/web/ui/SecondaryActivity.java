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

    // Verify if browser manager preload data complete.
    if (mBrowserManager.isPreload()) {
      if (mBrowserManager.isLoadComplete()) {
        Logger.d(TAG, "Web page has preload complete");
        mBrowserManager.showDialog(getSupportFragmentManager(), RedPacketDialogFragment.class);
      } else {
        mBrowserManager.showDialogOnLoadComplete(getSupportFragmentManager(),
            RedPacketDialogFragment.class);
      }
    } else {
      Logger.d(TAG, "Web page haven't load yet");
      mBrowserManager.loadUrl(Browser.URL);
      // Don't forget setup WebView settings
      mBrowserManager.setupWebViewWithDefaults();
      mBrowserManager.showDialogOnLoadComplete(getSupportFragmentManager(),
          RedPacketDialogFragment.class);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mBrowserManager.closeDialog();
  }
}

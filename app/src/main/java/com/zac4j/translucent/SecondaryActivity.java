package com.zac4j.translucent;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SecondaryActivity extends AppCompatActivity {

  private static final String TAG = SecondaryActivity.class.getSimpleName();

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

    BrowserManager browserManager = BrowserManager.getInstance(getApplicationContext());

    // Verify if browser manager has preload url.
    if (browserManager.hasPreloaded()) {
      Logger.d(TAG, "Web page has preload complete");
    } else {
      Logger.d(TAG, "Web page haven't load yet");
      browserManager.loadUrl(Browser.URL);
    }

    browserManager.showDialog(getSupportFragmentManager());
  }

  @Override
  protected void onResume() {
    super.onResume();
    Logger.d(TAG, "onResume");
  }
}

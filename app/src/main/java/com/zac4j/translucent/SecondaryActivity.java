package com.zac4j.translucent;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class SecondaryActivity extends AppCompatActivity {

  private static final String TAG = SecondaryActivity.class.getSimpleName();

  @Override public void onCreate(@Nullable Bundle savedInstanceState,
      @Nullable PersistableBundle persistentState) {
    super.onCreate(savedInstanceState, persistentState);
    setContentView(R.layout.activity_secondary);
  }

  @Override protected void onStart() {
    super.onStart();
    Logger.d(TAG, "onStart: ");
  }

  @Override protected void onRestart() {
    Logger.d(TAG, "onRestart: ");
    super.onRestart();
  }

  @Override protected void onResume() {
    Logger.d(TAG, "onResume: ");
    super.onResume();
  }

  @Override protected void onStop() {
    Logger.d(TAG, "onStop: ");
    super.onStop();
  }
}

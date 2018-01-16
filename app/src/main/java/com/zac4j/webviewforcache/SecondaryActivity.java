package com.zac4j.webviewforcache;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class SecondaryActivity extends AppCompatActivity {

  private static final String TAG = "SecondaryActivity";
  
  @Override public void onCreate(@Nullable Bundle savedInstanceState,
      @Nullable PersistableBundle persistentState) {
    super.onCreate(savedInstanceState, persistentState);
    setContentView(R.layout.activity_secondary);
  }

  @Override protected void onStart() {
    super.onStart();
    Log.d(TAG, "onStart: ");
    TranslucentWebDataManager.getInstance().show(getSupportFragmentManager());
  }

  @Override protected void onRestart() {
    Log.d(TAG, "onRestart: ");
    super.onRestart();
  }

  @Override protected void onResume() {
    Log.d(TAG, "onResume: ");
    super.onResume();
  }

  @Override protected void onStop() {
    TranslucentWebDataManager.getInstance().dismiss();
    Log.d(TAG, "onStop: ");
    super.onStop();
  }
}

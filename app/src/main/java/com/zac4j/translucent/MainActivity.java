package com.zac4j.translucent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();
  private static final String URL = "https://dl.app.gtja.com/web/gtjaPopup/hongbao/index.html";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    BrowserManager browserManager = BrowserManager.getInstance(getApplicationContext());
    browserManager.foreLoadUrl(URL);
    Logger.d(TAG, "fore load remote url data");
  }

  public void jump(View view) {
    startActivity(new Intent(MainActivity.this, SecondaryActivity.class));
  }
}

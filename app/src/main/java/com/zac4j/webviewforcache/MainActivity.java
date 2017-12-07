package com.zac4j.webviewforcache;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

  private static final String URL = "https://dl.app.gtja.com/web/gtjaPopup/hongbao/index.html";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    BootPopUpHelper popUpView = BootPopUpHelper.getInstance();
    popUpView.loadUrl(MainActivity.this, URL);
  }

  public void jump(View view) {
    startActivity(new Intent(MainActivity.this, SecondaryActivity.class));
  }
}

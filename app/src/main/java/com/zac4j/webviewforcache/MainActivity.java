package com.zac4j.webviewforcache;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    RedPacketFragment fragment = RedPacketFragment.newInstance(RedPacketFragment.MICRO);
    fragment.show(getSupportFragmentManager(), "hello");
  }

  public void getRedPacket(View view) {
    new Handler().postDelayed(new Runnable() {
      @Override public void run() {
        startActivity(new Intent(MainActivity.this, SecondaryActivity.class));
      }
    }, 2 * 1000);
  }
}

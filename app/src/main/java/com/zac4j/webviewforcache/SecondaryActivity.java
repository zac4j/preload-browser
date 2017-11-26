package com.zac4j.webviewforcache;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SecondaryActivity extends AppCompatActivity {

  @Override public void onCreate(@Nullable Bundle savedInstanceState,
      @Nullable PersistableBundle persistentState) {
    super.onCreate(savedInstanceState, persistentState);
    setContentView(R.layout.activity_secondary);
  }

  @Override protected void onResume() {
    super.onResume();

    RedPacketFragment fragment = RedPacketFragment.newInstance(RedPacketFragment.FULLSCREEN);
    fragment.show(getSupportFragmentManager(), "hello");
  }
}

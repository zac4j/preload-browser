package com.zac4j.webviewforcache;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";
  private static final String URL = "https://dl.app.gtja.com/web/gtjaPopup/hongbao/index.html";

  private int redPacketId = 0x110022;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    readDeviceFingerPrint();

    int screenWidth = getResources().getDisplayMetrics().widthPixels;
    int screenHeight = getResources().getDisplayMetrics().heightPixels;

    System.out.println("width >>> " + screenWidth + ", height >>> " + screenHeight);

    final ViewGroup rootView = findViewById(android.R.id.content);
    View redPacketView = getLayoutInflater().inflate(R.layout.include_lingxi_conent, null);
    final RelativeLayout rl = redPacketView.findViewById(R.id.root_view);

    ImageView redPacket = new ImageView(this);
    redPacket.setId(redPacketId);
    redPacket.setImageResource(R.drawable.red_packet);
    RelativeLayout.LayoutParams redPacketParams = new RelativeLayout.LayoutParams(148, 148);
    redPacketParams.leftMargin = screenWidth - 148 - 50 - 45;
    redPacketParams.topMargin = 900;
    rl.addView(redPacket, redPacketParams);

    final ImageView closeBtn = new ImageView(this);
    closeBtn.setImageResource(R.drawable.close);
    RelativeLayout.LayoutParams closeParams = new RelativeLayout.LayoutParams(60, 60);
    closeParams.topMargin = 900;
    closeParams.addRule(RelativeLayout.RIGHT_OF, redPacketId);
    rl.addView(closeBtn, closeParams);

    closeBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        rootView.removeView(rl);
      }
    });

    rootView.addView(rl);

    TranslucentWebDataManager dataManager = TranslucentWebDataManager.getInstance(getApplication());
    dataManager.foreLoadUrl(URL);
  }

  private void readDeviceFinger(Context context, String type) {
    ApplicationInfo appInfo = context.getApplicationInfo();
    String sourceDir = appInfo.sourceDir;

    String key = "META-INF/" + type;
  }

  private void readDeviceFingerPrint() {
    try {
      Log.d(TAG, " 开始读取了 readDeviceFingerPrint()");
      ApplicationInfo appInfo =
          getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);

      if (appInfo.metaData != null) {
        String fingerPrint = appInfo.metaData.getString("junhapptgset");
        Log.e(TAG, "readDeviceFingerPrint: " + fingerPrint);
      }
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void jump(View view) {
    startActivity(new Intent(MainActivity.this, SecondaryActivity.class));
  }
}

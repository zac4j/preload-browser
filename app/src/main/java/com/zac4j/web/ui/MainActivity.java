package com.zac4j.web.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.zac4j.web.R;
import com.zac4j.web.Utils;
import com.zac4j.web.browser.BrowserDialogManager;

/**
 * Created by zac on 2018/5/27.
 * Description:Launch Page
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BrowserDialogManager browserManager = BrowserDialogManager.getInstance(getApplicationContext());
        // Step two: set url to preload data
        browserManager.preloadUrl(Utils.provideAnotherUrl());
    }

    public void gotoApproach(View view) {
        switch (view.getId()) {
            case R.id.approach_one:
                startActivity(new Intent(MainActivity.this, PrimaryActivity.class));
                break;
            case R.id.approach_two:
                startActivity(new Intent(MainActivity.this, TertiaryActivity.class));
                break;
        }
    }
}

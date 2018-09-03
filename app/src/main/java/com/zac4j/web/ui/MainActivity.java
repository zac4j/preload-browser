package com.zac4j.web.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.zac4j.web.R;

/**
 * Created by zac on 2018/5/27.
 * Description:Launch Page
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

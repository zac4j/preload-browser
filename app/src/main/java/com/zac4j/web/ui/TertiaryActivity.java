package com.zac4j.web.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.zac4j.web.Logger;
import com.zac4j.web.R;
import com.zac4j.web.Utils;
import com.zac4j.web.loader.OKBrowserLoader;
import java.io.File;

/**
 * Created by Zaccc on 2018/4/23.
 */
public class TertiaryActivity extends Activity {

    private static final String TAG = TertiaryActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tertiary);
    }

    @Override
    protected void onStart() {
        super.onStart();

        String url = Utils.provideUrl();

        String cacheLocation = getCacheDir().getAbsolutePath() + File.separator + "index.html";
        Logger.d(TAG, "Create cache location: " + cacheLocation);
        OKBrowserLoader.getInstance().loadUrl(url, cacheLocation);
    }

    public void gotoQuaternary(View view) {
        Logger.d(TAG, "Go to Quaternary page");
        startActivity(new Intent(TertiaryActivity.this, QuaternaryActivity.class));
    }
}
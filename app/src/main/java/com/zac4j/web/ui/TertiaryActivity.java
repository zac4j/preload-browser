package com.zac4j.web.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import com.zac4j.web.R;
import com.zac4j.web.Utils;
import com.zac4j.web.loader.WebPageLoadManager;
import java.io.File;

/**
 * Created by Zaccc on 2018/4/23.
 */
public class TertiaryActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tertiary);
    }

    @Override
    protected void onStart() {
        super.onStart();

        String url = Utils.provideUrl();

        String destinationDir = getCacheDir().getPath() + File.separator + "index.html";
        WebPageLoadManager.getInstance().loadUrl(url, destinationDir);
    }
}

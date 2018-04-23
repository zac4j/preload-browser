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

/**
 * Created by Zaccc on 2018/4/23.
 */
public class TertiaryActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tertiary);

        FrameLayout webViewContainer = findViewById(R.id.web_container);
        WebView webView = new WebView(getApplicationContext());
        webViewContainer.addView(webView);

        webView.setWebViewClient(new PreloadWebViewClient());
    }

    class PreloadWebViewClient extends WebViewClient {

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view,
            WebResourceRequest request) {

            Uri uri = request.getUrl();
            return super.shouldInterceptRequest(view, request);
        }
    }
}

package com.zac4j.web.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import com.zac4j.web.Logger;
import com.zac4j.web.R;
import com.zac4j.web.Utils;
import com.zac4j.web.loader.WebPageLoadManager;
import java.io.File;
import java.io.FileNotFoundException;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by zac on 2018/5/26.
 * Description:
 */
public class QuaternaryActivity extends Activity {

    private static final String TAG = QuaternaryActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quaternary);

        FrameLayout webViewContainer = findViewById(R.id.web_container);
        WebView webView = new WebView(getApplicationContext());
        webViewContainer.addView(webView);

        webView.setWebViewClient(new QuaternaryActivity.PreloadWebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(Utils.provideUrl());
    }

    class PreloadWebViewClient extends WebViewClient {

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view,
            WebResourceRequest request) {

            String url = request.getUrl().toString();
            String cacheLocation = WebPageLoadManager.getInstance().getCacheLocation(url);
            if (TextUtils.isEmpty(cacheLocation)) {
                return super.shouldInterceptRequest(view, request);
            }
            File cache = new File(cacheLocation);
            try {
                BufferedSource source = Okio.buffer(Okio.source(cache));
                return new WebResourceResponse("text/html", "utf-8", source.inputStream());
            } catch (FileNotFoundException e) {
                Logger.d(TAG, "Read cache file met error: " + e.getMessage());
            }
            return super.shouldInterceptRequest(view, request);
        }
    }

    class LazyWebChromeClient extends WebChromeClient {

    }

}

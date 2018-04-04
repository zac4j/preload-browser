package com.zac4j.web.browser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.zac4j.web.Logger;
import com.zac4j.web.Utils;
import com.zac4j.web.router.UrlRouter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A helper class to manage browser container data.
 * Created by Zaccc on 2017/12/7.
 */

class BrowserPageManager {

    private static final String TAG = BrowserPageManager.class.getSimpleName();

    // WebView request timeout spec.
    private static final int TIME_OUT_PROGRESS = 50;
    private static final long TIME_OUT_MILLIS = 3 * 1000L;

    private Context mAppContext;
    private WebView mWebView;

    // If preload url.
    private boolean mIsPreload;
    // If load url complete
    private AtomicBoolean mIsLoadComplete;
    // Browser event handler
    private Handler mHandler;
    // Browser url route spec.
    private UrlRouter mUrlRouter;

    private OnLoadStateChangeListener mOnLoadStateChangeListener;

    private BrowserPageManager() {

    }

    BrowserPageManager(final Context context) {
        prepareWebView(context);
        mAppContext = context;
        mIsLoadComplete = new AtomicBoolean(false);
        mHandler = new Handler();
    }

    /**
     * Registers a listener that will receive callbacks while a load state is changed.
     * The callback will be called on the main thread so it's safe to
     * pass the results to widgets.
     *
     * Must be called from the process's main thread.
     *
     * @param listener The listener to register.
     */
    public void registerOnLoadStateChangeListener(OnLoadStateChangeListener listener) {
        if (mOnLoadStateChangeListener != null) {
            throw new IllegalStateException("There is already a listener registered");
        }
        mOnLoadStateChangeListener = listener;
    }

    /**
     * Unregisters a listener that was previously added with
     * {@link #registerOnLoadStateChangeListener}.
     *
     * Must be called from the main thread.
     *
     * @param listener The listener to unregister.
     */
    public void unregisterOnLoadStateChangeListener(OnLoadStateChangeListener listener) {
        if (mOnLoadStateChangeListener == null) {
            throw new IllegalStateException("No listener register");
        }
        if (mOnLoadStateChangeListener != listener) {
            throw new IllegalArgumentException("Attempting to unregister the wrong listener");
        }
        mOnLoadStateChangeListener = null;
    }

    /**
     * Preload given url in Browser WebView.
     *
     * @param url given url to load.
     */
    void preloadUrl(String url) {

        if (mWebView == null) {
            throw new IllegalStateException(
                "You should initialize BrowserManager before load url.");
        }

        mIsPreload = true;

        loadUrl(url);
    }

    /**
     * Load given url in Browser WebView.
     *
     * @param url given url to load.
     */
    void loadUrl(String url) {

        if (mWebView == null) {
            throw new IllegalStateException(
                "You should initialize BrowserManager before load url.");
        }

        try {
            url = URLDecoder.decode(url, "utf-8");
            mWebView.loadUrl(url);
        } catch (UnsupportedEncodingException e) {
            Logger.e(TAG, "decode url failed", e);
        }
    }

    /**
     * Prepare WebView instance
     *
     * @param context It's better to provide an application context.
     */
    private void prepareWebView(Context context) {
        mWebView = new WebView(context);
    }

    /**
     * Set up WebView default settings & clients
     *
     * @param webView WebView to set up default settings.
     */
    void setupWebViewWithDefaults(WebView webView) {
        setWebViewSettings(webView);
        setBrowserClients(webView);
    }

    /**
     * Provide WebView instance.
     *
     * @return WebView instance.
     */
    WebView getWebView() {
        return mWebView;
    }

    /**
     * Assemble WebView into WebView container
     *
     * @param container WebView container
     */
    void assembleWebView(ViewGroup container) {
        ViewGroup.LayoutParams params =
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        if (container instanceof FrameLayout) {
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        } else if (container instanceof RelativeLayout) {
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        } else if (container instanceof LinearLayout) {
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        }
        mWebView.setLayoutParams(params);
        container.addView(mWebView);
    }

    /**
     * Remove WebView from WebView container
     *
     * @param container WebView container
     */
    void removeWebView(ViewGroup container) {
        container.removeAllViews();
    }

    void addUrlRouter(UrlRouter router) {
        mUrlRouter = router;
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (mUrlRouter == null) {
                    return super.shouldOverrideUrlLoading(view, url);
                }
                return mUrlRouter.route(url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (mUrlRouter == null) {
                    return super.shouldOverrideUrlLoading(view, request);
                }
                return mUrlRouter.route(request.getUrl().toString());
            }
        });
    }

    /**
     * Set WebView's WebViewClient and WebChromeClient.
     *
     * @param webView WebView to set up client.
     */
    void setBrowserClients(final WebView webView) {
        try {
            webView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView webview, String url) {
                    Logger.d(TAG, "shouldOverrideUrlLoading intercept url: " + url);

                    webView.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageStarted(final WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    Logger.d(TAG, "onPageStarted: ");
                    // Handle WebView loading timeout problem.
                    if (mHandler != null) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (view != null && view.getProgress() < TIME_OUT_PROGRESS) {
                                    mIsLoadComplete.set(false);
                                    mOnLoadStateChangeListener.onLoadFailed(0,
                                        TAG + " load timeout.");
                                }
                            }
                        }, TIME_OUT_MILLIS);
                    }
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    String title = view.getTitle(); // Get page title

                    Logger.d(TAG, "onPageFinished WebView title=" + title);
                }

                @Override
                public void onReceivedError(WebView view, final int errorCode,
                    final String description, String failingUrl) {
                    mOnLoadStateChangeListener.onLoadFailed(errorCode, description);
                    Toast.makeText(view.getContext(), "Load failed with error: " + description,
                        Toast.LENGTH_LONG).show();
                }
            });

            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    Logger.d(TAG, "onProgressChanged: " + newProgress);
                    if (view != null && newProgress == 100) {
                        // Page load complete
                        if (!mIsLoadComplete.get()) {
                            mIsLoadComplete.set(true);
                            if (mIsPreload) {
                                Logger.d(TAG, "Preload complete");
                            }
                            if (mOnLoadStateChangeListener != null) {
                                mOnLoadStateChangeListener.onLoadComplete();
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage(), e);
        }
    }

    @SuppressLint({ "SetJavaScriptEnabled", "ObsoleteSdkInt" })
    void setWebViewSettings(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        // UI display
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setTextZoom(100);
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        // WebView content access, cache, storage.
        settings.setAllowContentAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowUniversalAccessFromFileURLs(true);
        }

        if (Utils.isNetworkAvailable(mAppContext)) {
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webView.removeJavascriptInterface("searchBoxJavaBridge_");
            webView.removeJavascriptInterface("accessibility");
            webView.removeJavascriptInterface("accessibilityTraversal");
        }

        // Enable mix load http/https content
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // Enable remote debugging via chrome://inspect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    boolean isPreload() {
        return mIsPreload;
    }

    boolean isLoadComplete() {
        return mIsLoadComplete.get();
    }

    void clearWebView() {

        mIsPreload = false;

        mIsLoadComplete.set(false);

        if (mWebView != null) {
            mWebView.clearHistory();

            // NOTE: clears RAM cache, if you pass true, it will also clear the disk cache.
            // Probably not a great idea to pass true if you have other WebViews still alive.
            mWebView.clearCache(true);

            // Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
            mWebView.loadUrl("about:blank");
        }
    }

    void destroyWebView() {

        mIsPreload = false;

        mIsLoadComplete.set(false);

        if (mWebView != null) {
            mWebView.clearHistory();

            // NOTE: clears RAM cache, if you pass true, it will also clear the disk cache.
            // Probably not a great idea to pass true if you have other WebViews still alive.
            mWebView.clearCache(true);

            // Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
            mWebView.loadUrl("about:blank");

            mWebView.onPause();
            mWebView.removeAllViews();
            mWebView.destroyDrawingCache();

            // NOTE: This pauses JavaScript execution for ALL WebViews,
            // do not use if you have other WebViews still alive.
            // If you create another WebView after calling this,
            // make sure to call mWebView.resumeTimers().
            mWebView.pauseTimers();

            // NOTE: This can occasionally cause a segfault below API 17 (4.2)
            mWebView.destroy();

            // Null out the reference so that you don't end up re-using it.
            mWebView = null;
        }
    }

    public interface OnLoadStateChangeListener {

        void onLoadComplete();

        void onLoadFailed(int errorCode, String description);
    }
}

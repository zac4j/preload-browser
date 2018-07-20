package com.zac4j.web.browser;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.RenderProcessGoneDetail;
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
 * A class for manage load/preload web resource by Android {@link WebView}.
 * Created by Zaccc on 2017/12/7.
 */

public class BrowserManager {

    private static final String TAG = BrowserManager.class.getSimpleName();

    // WebView request timeout spec.
    private static final int TIME_OUT_PROGRESS = 50;
    private static final long TIME_OUT_MILLIS = 3 * 1000L;

    private Context mAppContext;
    private WebView mWebView;

    // If preload url.
    private boolean mIsPreload;
    // If load url complete
    private AtomicBoolean mIsLoadComplete;
    // Scheme event handler
    private Handler mHandler;
    // Scheme url route spec.
    private UrlRouter mUrlRouter;

    private OnLoadStateChangeListener mOnLoadStateChangeListener;

    public BrowserManager(@NonNull Context appContext) {
        prepareWebView(appContext);
        mAppContext = appContext;
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
    public void registerOnLoadStateChangeListener(@NonNull OnLoadStateChangeListener listener) {
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
    public void unregisterOnLoadStateChangeListener(@NonNull OnLoadStateChangeListener listener) {
        if (mOnLoadStateChangeListener == null) {
            throw new IllegalStateException("No listener register");
        }
        if (mOnLoadStateChangeListener != listener) {
            throw new IllegalArgumentException("Attempting to unregister the wrong listener");
        }
        mOnLoadStateChangeListener = null;
    }

    /**
     * Preload given url in Scheme WebView.
     *
     * @param url given url to load.
     */
    public void preloadUrl(@NonNull String url) {

        if (mWebView == null) {
            throw new IllegalStateException(
                "You should initialize BrowserManager before load url.");
        }

        if (!Utils.isValidUrl(url)) {
            throw new IllegalArgumentException("You shouldn't load url with an invalid url");
        }

        mIsPreload = true;

        loadUrl(url);
    }

    /**
     * Load given url in Scheme WebView.
     *
     * @param url given url to load.
     */
    public void loadUrl(@NonNull String url) {

        if (mWebView == null) {
            throw new IllegalStateException(
                "You should initialize BrowserManager before load url.");
        }

        if (!Utils.isValidUrl(url)) {
            throw new IllegalArgumentException("You shouldn't load url with an invalid url");
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
    private void prepareWebView(@NonNull Context context) {
        mWebView = new WebView(context);
        setupWebViewWithDefaults();
    }

    /**
     * Set up WebView default settings & clients
     */
    private void setupWebViewWithDefaults() {
        setWebViewSettings(mWebView);
        setBrowserClients(mWebView);
    }

    /**
     * Provide WebView instance.
     *
     * @return WebView instance.
     */
    public WebView getWebView() {
        return mWebView;
    }

    /**
     * Assemble WebView into WebView container
     *
     * @param container WebView container
     */
    public void assembleWebView(@NonNull ViewGroup container) {

        if (container == null) {
            throw new IllegalArgumentException("WebView container must not be null!");
        }

        if (mWebView.getParent() == container) {
            return;
        }

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
    public void removeWebView(@NonNull ViewGroup container) {
        if (container == null) {
            throw new IllegalArgumentException("WebView container must not be null!");
        }
        container.removeAllViews();
    }

    public void addUrlRouter(@NonNull UrlRouter router) {

        if (router == null) {
            throw new IllegalStateException("UrlRouter must not be null!");
        }

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
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }
        });
    }

    /**
     * Set WebView's WebViewClient and WebChromeClient.
     *
     * @param webView WebView to set up client.
     */
    private void setBrowserClients(@NonNull final WebView webView) {

        if (webView == null) {
            throw new IllegalArgumentException("WebView should not be null!");
        }

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

                @TargetApi(Build.VERSION_CODES.O)
                @Override
                public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
                    if (!detail.didCrash()) {
                        // Renderer was killed because the system ran out of memory.
                        // The app can recover gracefully by creating a new WebView instance
                        // in the foreground.
                        Logger.e(TAG, "System killed the WebView rendering process "
                            + "to reclaim memory. Recreating...");

                        if (mWebView != null) {
                            ViewGroup webViewContainer = (ViewGroup) mWebView.getParent();
                            if (webViewContainer != null && webViewContainer.getChildCount() > 0) {
                                webViewContainer.removeView(mWebView);
                            }
                            mWebView.destroy();
                            mWebView = null;
                        }

                        // By this point, the instance variable "mWebView" is guaranteed
                        // to be null, so it's safe to reinitialize it.

                        return true; // The app continues executing.
                    }
                    return false;
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
    private void setWebViewSettings(@NonNull WebView webView) {

        if (webView == null) {
            throw new IllegalArgumentException("WebView should not be null!");
        }

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

    /**
     * Check if web resources is preloaded.
     *
     * @return true if web resource is preloaded, otherwise return false.
     */
    public boolean isPreload() {
        return mIsPreload;
    }

    /**
     * Check web resources is load complete.
     *
     * @return true if web resource is load complete, otherwise return false.
     */
    public boolean isLoadComplete() {
        return mIsLoadComplete.get();
    }

    /**
     * Clean up WebView object
     */
    public void clearWebView() {

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

    /**
     * Destroy WebView object.
     */
    public void destroyWebView() {

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

    /**
     * Interface for listening web resource load state changed.
     */
    public interface OnLoadStateChangeListener {

        void onLoadComplete();

        void onLoadFailed(int errorCode, String description);
    }
}

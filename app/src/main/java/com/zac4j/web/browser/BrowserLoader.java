package com.zac4j.web.browser;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A class for manage load/preload web resource by Android {@link WebView}.
 * Created by Zaccc on 2017/12/7.
 */

public class BrowserLoader {

    private static final String TAG = BrowserLoader.class.getSimpleName();

    private Context mAppContext;
    // Collection for preload url links.
    private Set<String> mPreloadUrlSet;
    // Collection for load completely url links.
    private Set<String> mFinishLoadUrlSet;
    // WebView pool.
    private Map<String, WebView> mWebViewPool;
    // Collection for url load state listener.
    private Map<String, LoadStateListener> mUrlLoadStateListeners;

    BrowserLoader(@NonNull Context appContext) {
        mAppContext = appContext;
        mPreloadUrlSet = new HashSet<>();
        mFinishLoadUrlSet = new HashSet<>();
        mWebViewPool = new ArrayMap<>();
        mUrlLoadStateListeners = new ArrayMap<>();
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
    public void registerLoadStateListener(@NonNull String url,
        @NonNull LoadStateListener listener) {

        if (mUrlLoadStateListeners == null) {
            throw new IllegalStateException("You should initialize BrowserLoader before register listener");
        }

        if (mUrlLoadStateListeners.containsKey(url)) {
            throw new IllegalStateException("There is already a listener registered");
        }
        mUrlLoadStateListeners.put(url, listener);
    }

    /**
     * Unregisters a listener that was previously added with
     * {@link #registerLoadStateListener}.
     *
     * Must be called from the main thread.
     *
     * @param url The url for unregister listener.
     */
    public void unregisterOnLoadStateListener(String url) {

        if (!mUrlLoadStateListeners.containsKey(url)) {
            throw new IllegalStateException("No listener register");
        }
        if (mUrlLoadStateListeners.get(url) == null) {
            throw new IllegalArgumentException("Attempting to unregister the wrong listener");
        }
        mUrlLoadStateListeners.remove(url);
    }

    /**
     * Preload given url in Scheme WebView.
     *
     * @param url given url to load.
     */
    public void preloadUrl(@NonNull String url) {

        WebView webView = prepareWebView(mAppContext);

        if (webView == null) {
            throw new IllegalStateException("You should initialize BrowserLoader before load url");
        }

        if (!Utils.isValidUrl(url)) {
            throw new IllegalArgumentException("You shouldn't load url with an invalid url");
        }

        // Add this link to preload url collection.
        mPreloadUrlSet.add(url);

        loadUrl(url, webView);
    }

    /**
     * Load given url in Scheme WebView.
     *
     * @param url given url to load.
     */
    public void loadUrl(@NonNull String url) {

        WebView webView = prepareWebView(mAppContext);

        if (webView == null) {
            throw new IllegalStateException(
                "You should initialize BrowserLoader before load url.");
        }

        if (!Utils.isValidUrl(url)) {
            throw new IllegalArgumentException("You shouldn't load url with an invalid url");
        }

        try {
            url = URLDecoder.decode(url, "utf-8");

            // Establish relationship between url and webView.
            mWebViewPool.put(url, webView);

            webView.loadUrl(url);
        } catch (UnsupportedEncodingException e) {
            Logger.e(TAG, "decode url failed", e);
        }
    }

    /**
     * Load given url in Scheme WebView.
     *
     * @param url given url to load.
     */
    private void loadUrl(@NonNull String url, @NonNull WebView webView) {

        try {
            url = URLDecoder.decode(url, "utf-8");

            // Establish relationship between url and webView.
            mWebViewPool.put(url, webView);

            webView.loadUrl(url);
        } catch (UnsupportedEncodingException e) {
            Logger.e(TAG, "decode url failed", e);
        }
    }

    /**
     * Prepare WebView instance
     *
     * @param context It's better to provide an application context.
     */
    private WebView prepareWebView(@NonNull Context context) {
        WebView webView = new WebView(context);
        setupWebViewWithDefaults(webView);
        return webView;
    }

    /**
     * Set up WebView default settings & clients
     */
    private void setupWebViewWithDefaults(WebView webView) {
        setWebViewSettings(webView);
        setBrowserClients(webView);
    }

    /**
     * Provide WebView instance.
     *
     * @return WebView instance.
     */
    private WebView getWebView(String url) {
        if (!mWebViewPool.containsKey(url)) {
            throw new IllegalStateException("You should call loadUrl() before get WebView");
        }
        return mWebViewPool.get(url);
    }

    /**
     * Assemble WebView into WebView container
     *
     * @param container WebView container
     */
    public void assembleWebView(String url, @NonNull ViewGroup container) {

        if (container == null) {
            throw new IllegalArgumentException("WebView container must not be null!");
        }

        WebView webView = getWebView(url);

        if (webView.getParent() == container) {
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
        webView.setLayoutParams(params);
        container.addView(webView);
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

    public void addUrlRouter(String url, @NonNull final UrlRouter router) {

        if (router == null) {
            throw new IllegalStateException("UrlRouter must not be null!");
        }

        WebView webView = getWebView(url);

        if (webView == null) {
            return;
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (router == null) {
                    return super.shouldOverrideUrlLoading(view, url);
                }
                return router.route(view, url);
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
                public void onReceivedError(WebView view, final int errorCode,
                    final String description, String failingUrl) {
                    String url = view.getOriginalUrl();
                    if (mUrlLoadStateListeners != null && mUrlLoadStateListeners.containsKey(url)) {
                        mUrlLoadStateListeners.get(url).onLoadFailed(errorCode, description);
                    }
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

                        if (view != null) {
                            ViewGroup webViewContainer = (ViewGroup) view.getParent();
                            if (webViewContainer != null && webViewContainer.getChildCount() > 0) {
                                webViewContainer.removeView(view);
                            }
                            view.destroy();
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
                        String url = view.getOriginalUrl();
                        if (!mFinishLoadUrlSet.contains(url)) {
                            mFinishLoadUrlSet.add(url);
                            if (mPreloadUrlSet.contains(url)) {
                                Logger.d(TAG, "preload url:" + url + " complete");
                            }
                            if (mUrlLoadStateListeners != null
                                && mUrlLoadStateListeners.containsKey(url)) {
                                mUrlLoadStateListeners.get(url).onLoadComplete();
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

        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
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
     * Check if web resources is preload.
     *
     * @return true if web resource is preloaded, otherwise return false.
     */
    public boolean isPreloadUrl(@NonNull String url) {
        if (TextUtils.isEmpty(url) || "null".equalsIgnoreCase(url)) {
            throw new IllegalArgumentException("url == null!");
        }
        return mPreloadUrlSet.contains(url);
    }

    /**
     * Check web resources is load complete.
     *
     * @return true if web resource is load complete, otherwise return false.
     */
    public boolean isLoadComplete(@NonNull String url) {

        if (TextUtils.isEmpty(url) && "null".equalsIgnoreCase(url)) {
            throw new IllegalArgumentException("url == null!");
        }

        return mFinishLoadUrlSet.contains(url);
    }

    /**
     * Clean up WebView object
     */
    public void clearWebView(String url) {

        WebView webView = getWebView(url);

        if (webView != null) {
            webView.clearHistory();

            // NOTE: clears RAM cache, if you pass true, it will also clear the disk cache.
            // Probably not a great idea to pass true if you have other WebViews still alive.
            webView.clearCache(true);

            // Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
            webView.loadUrl("about:blank");
        }
    }

    /**
     * Destroy WebView object.
     */
    public void destroyWebView(String url) {

        WebView webView = getWebView(url);

        if (webView != null) {
            webView.clearHistory();

            // NOTE: clears RAM cache, if you pass true, it will also clear the disk cache.
            // Probably not a great idea to pass true if you have other WebViews still alive.
            webView.clearCache(true);

            // Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
            webView.loadUrl("about:blank");

            webView.onPause();
            webView.removeAllViews();
            webView.destroyDrawingCache();

            // NOTE: This pauses JavaScript execution for ALL WebViews,
            // do not use if you have other WebViews still alive.
            // If you create another WebView after calling this,
            // make sure to call mWebView.resumeTimers().
            webView.pauseTimers();

            // NOTE: This can occasionally cause a segfault below API 17 (4.2)
            webView.destroy();

            mWebViewPool.remove(url);
        }
    }

    /**
     * Interface for listening web resource load state changed.
     */
    public interface LoadStateListener {

        void onLoadComplete();

        void onLoadFailed(int errorCode, String description);
    }
}

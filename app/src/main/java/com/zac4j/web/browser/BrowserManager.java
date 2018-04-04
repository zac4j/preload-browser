package com.zac4j.web.browser;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.zac4j.web.Logger;
import com.zac4j.web.Utils;
import com.zac4j.web.router.UrlRouter;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Helper class to manage Scheme.
 * Created by Zaccc on 2018/1/18.
 */

public class BrowserManager {

    private static final String TAG = BrowserManager.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static BrowserManager sInstance;

    private final BrowserPageManager mPageManager;

    private AtomicBoolean mHasDialog;
    private BrowserDialogFragment mBrowserDialog;
    private BrowserPageManager.OnLoadStateChangeListener mOnLoadStateChangeListener;

    private BrowserManager(final Context context) {
        mPageManager = new BrowserPageManager(context);
        mHasDialog = new AtomicBoolean(false);
    }

    /**
     * Provide BrowserPageManager Singleton
     *
     * @param context It's better to provide application context.
     * @return BrowserPageManager instance.
     */
    public static BrowserManager getInstance(Context context) {
        Logger.d(TAG, "Getting browser data manager instance");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new BrowserManager(context);
                Logger.d(TAG, "Made new browser data manager");
            }
        }
        return sInstance;
    }

    /**
     * Provide WebView instance.
     *
     * @return WebView instance.
     */
    public WebView getWebView() {
        return mPageManager.getWebView();
    }

    /**
     * Add WebView url intercept spec.
     *
     * @param router url route spec.
     */
    public void addUrlRouter(UrlRouter router) {
        mPageManager.addUrlRouter(router);
    }

    /**
     * Set up WebView default settings.
     */
    public void setupWebViewWithDefaults() {
        mPageManager.setupWebViewWithDefaults(getWebView());
    }

    /**
     * Returns if BrowserManager preload url.
     *
     * @return true if preload, otherwise false.
     */
    public boolean isPreload() {
        return mPageManager.isPreload();
    }

    /**
     * Returns if BrowserManager load url complete.
     *
     * @return true if load complete, otherwise false.
     */
    public boolean isLoadComplete() {
        return mPageManager.isLoadComplete();
    }

    /**
     * Preload given url in Scheme WebView
     *
     * @param url given url to preload.
     */
    public void preloadUrl(String url) {
        if (Utils.isValidUrl(url)) {
            mPageManager.preloadUrl(url);
        } else {
            throw new IllegalArgumentException("You shouldn't load url with an invalid url");
        }
    }

    /**
     * Load given url in Scheme WebView.
     *
     * @param url given url to load.
     */
    public void loadUrl(String url) {
        if (Utils.isValidUrl(url)) {
            mPageManager.loadUrl(url);
        } else {
            throw new IllegalArgumentException("You shouldn't load url with an invalid url");
        }
    }

    /**
     * Show a browser dialog.
     *
     * @param fragmentManager provide proper FragmentManager to show Dialog Fragment.
     */
    public void showDialog(FragmentManager fragmentManager,
        Class<? extends BrowserDialogFragment> dialogClass) {
        try {
            if (mHasDialog.get()) {
                return;
            }
            mBrowserDialog = dialogClass.newInstance();
            mBrowserDialog.show(fragmentManager, TAG);
            mBrowserDialog.setOnLifecycleListener(new BrowserDialogFragment.OnLifecycleListener() {
                @Override
                public void onDialogShown(ViewGroup container) {
                    mHasDialog.set(true);
                    mPageManager.assembleWebView(container);
                }

                @Override
                public void onDialogDismiss(ViewGroup container) {
                    mHasDialog.set(false);
                    mPageManager.removeWebView(container);
                }
            });
        } catch (InstantiationException | IllegalAccessException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    /**
     * Show a browser dialog.
     *
     * @param fragmentManager provide proper FragmentManager to show Dialog Fragment.
     */
    public void showDialog(FragmentManager fragmentManager,
        Class<? extends BrowserDialogFragment> dialogClass, Bundle bundle) {
        try {
            if (mHasDialog.get()) {
                return;
            }
            mBrowserDialog = dialogClass.newInstance();
            mBrowserDialog.setArguments(bundle);
            mBrowserDialog.show(fragmentManager, TAG);
            mBrowserDialog.setOnLifecycleListener(createBrowserDialogOnLifecycleListener());
        } catch (InstantiationException | IllegalAccessException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public void showDialogOnLoadComplete(final FragmentManager fragmentManager,
        final Class<? extends BrowserDialogFragment> dialogClass) {

        if (mHasDialog.get()) {
            return;
        }

        mOnLoadStateChangeListener = new BrowserPageManager.OnLoadStateChangeListener() {
            @Override
            public void onLoadComplete() {
                try {
                    mBrowserDialog = dialogClass.newInstance();
                    mBrowserDialog.show(fragmentManager, TAG);
                    mBrowserDialog.setOnLifecycleListener(createBrowserDialogOnLifecycleListener());
                } catch (InstantiationException | IllegalAccessException e) {
                    Logger.e(TAG, e.getMessage());
                }
            }

            @Override
            public void onLoadFailed(int errorCode, String description) {
                Logger.e(TAG,
                    "load url failed, errorCode : " + errorCode + ", description: " + description);
            }
        };

        mPageManager.registerOnLoadStateChangeListener(mOnLoadStateChangeListener);
    }

    public void showDialogOnLoadComplete(final FragmentManager fragmentManager,
        final Class<? extends BrowserDialogFragment> dialogClass, final Bundle bundle) {

        if (mHasDialog.get()) {
            return;
        }

        mOnLoadStateChangeListener = new BrowserPageManager.OnLoadStateChangeListener() {
            @Override
            public void onLoadComplete() {
                try {
                    mBrowserDialog = dialogClass.newInstance();
                    mBrowserDialog.setArguments(bundle);
                    mBrowserDialog.show(fragmentManager, TAG);
                    mBrowserDialog.setOnLifecycleListener(createBrowserDialogOnLifecycleListener());
                } catch (InstantiationException | IllegalAccessException e) {
                    Logger.e(TAG, e.getMessage());
                }
            }

            @Override
            public void onLoadFailed(int errorCode, String description) {
                Logger.e(TAG,
                    "load url failed, errorCode : " + errorCode + ", description: " + description);
            }
        };

        mPageManager.registerOnLoadStateChangeListener(mOnLoadStateChangeListener);
    }

    private BrowserDialogFragment.OnLifecycleListener createBrowserDialogOnLifecycleListener() {
        return new BrowserDialogFragment.OnLifecycleListener() {
            @Override
            public void onDialogShown(ViewGroup container) {
                mHasDialog.set(true);
                mPageManager.assembleWebView(container);
            }

            @Override
            public void onDialogDismiss(ViewGroup container) {
                mHasDialog.set(false);
                mPageManager.removeWebView(container);
            }
        };
    }

    /**
     * Close browser dialog
     */
    public void closeDialog() {
        if (mHasDialog.get() && mBrowserDialog != null) {
            mBrowserDialog.dismiss();
            if (mOnLoadStateChangeListener != null) {
                mPageManager.unregisterOnLoadStateChangeListener(mOnLoadStateChangeListener);
            }
        }
    }

    /**
     * Clear WebView
     */
    public void clearWebView() {
        if (getWebView() != null) {
            mPageManager.clearWebView();
        }
    }

    /**
     * Destroy WebView
     */
    public void destroyWebView() {
        if (getWebView() != null) {
            mPageManager.destroyWebView();
        }
    }
}

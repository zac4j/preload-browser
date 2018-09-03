package com.zac4j.web.browser;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.zac4j.web.Logger;
import com.zac4j.web.router.UrlRouter;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class for manage load/preload web resource by Android {@link WebView} in {@link DialogFragment}.
 * Created by Zaccc on 2018/1/18.
 */

public class BrowserDialogManager {

    private static final String TAG = BrowserDialogManager.class.getSimpleName();

    private static final Object LOCK = new Object();
    private AtomicBoolean mHasDialog;
    private BrowserDialogFragment mBrowserDialog;
    private BrowserManager.LoadStateListener mLoadStateListener;

    private static BrowserDialogManager sInstance;
    private BrowserManager mBrowserManager;
    private String mUrl;

    public static BrowserDialogManager getInstance(Context appContext) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new BrowserDialogManager(appContext);
            }
        }
        return sInstance;
    }

    private BrowserDialogManager(Context appContext) {
        mBrowserManager = new BrowserManager(appContext);
        mHasDialog = new AtomicBoolean(false);
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
            mBrowserDialog.setLifecycleListener(new BrowserDialogFragment.LifecycleListener() {
                @Override
                public void onDialogShown(ViewGroup container) {
                    mHasDialog.set(true);
                    mBrowserManager.assembleWebView(mUrl, container);
                }

                @Override
                public void onDialogDismiss(ViewGroup container) {
                    mHasDialog.set(false);
                    mBrowserManager.removeWebView(container);
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
            mBrowserDialog.setLifecycleListener(createLifecycleListener());
        } catch (InstantiationException | IllegalAccessException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    /**
     * Show {@link BrowserDialogFragment} on web resource load complete.
     *
     * @param fragmentManager {@link FragmentManager} instance.
     * @param dialogClass {@link BrowserDialogFragment} class.
     */
    public void showDialogOnLoadComplete(final FragmentManager fragmentManager,
        final Class<? extends BrowserDialogFragment> dialogClass) {

        if (mHasDialog.get()) {
            return;
        }

        mLoadStateListener = new BrowserManager.LoadStateListener() {
            @Override
            public void onLoadComplete() {
                try {
                    mBrowserDialog = dialogClass.newInstance();
                    mBrowserDialog.show(fragmentManager, TAG);
                    mBrowserDialog.setLifecycleListener(createLifecycleListener());
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

        mBrowserManager.registerLoadStateListener(mUrl, mLoadStateListener);
    }

    /**
     * Show {@link BrowserDialogFragment} on web resource load complete.
     *
     * @param fragmentManager fragment manager
     * @param dialogClass {@link BrowserDialogFragment} class.
     * @param bundle bundle data for send into {@link BrowserDialogFragment} instance.
     */
    public void showDialogOnLoadComplete(final FragmentManager fragmentManager,
        final Class<? extends BrowserDialogFragment> dialogClass, final Bundle bundle) {

        if (mHasDialog.get()) {
            return;
        }

        mLoadStateListener = new BrowserManager.LoadStateListener() {
            @Override
            public void onLoadComplete() {
                try {
                    mBrowserDialog = dialogClass.newInstance();
                    mBrowserDialog.setArguments(bundle);
                    mBrowserDialog.show(fragmentManager, TAG);
                    mBrowserDialog.setLifecycleListener(createLifecycleListener());
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

        mBrowserManager.registerLoadStateListener(mUrl, mLoadStateListener);
    }

    /**
     * Create an {@link BrowserDialogFragment} lifecycle listener.
     *
     * @return an {@link BrowserDialogFragment} lifecycle listener.
     */
    private BrowserDialogFragment.LifecycleListener createLifecycleListener() {
        return new BrowserDialogFragment.LifecycleListener() {
            @Override
            public void onDialogShown(ViewGroup container) {
                mHasDialog.set(true);
                mBrowserManager.assembleWebView(mUrl, container);
            }

            @Override
            public void onDialogDismiss(ViewGroup container) {
                mHasDialog.set(false);
                mBrowserManager.removeWebView(container);
            }
        };
    }

    /**
     * Close browser dialog.
     */
    public void closeDialog() {
        if (mHasDialog.get() && mBrowserDialog != null) {
            mBrowserDialog.dismiss();
            if (mLoadStateListener != null) {
                mBrowserManager.unregisterOnLoadStateListener(mUrl);
            }
            mUrl = null;
        }
    }

    /**
     * Preload given url by {@link BrowserManager}.
     *
     * @param url given url to preload web resource.
     */
    public void preloadUrl(String url) {
        mUrl = url;
        mBrowserManager.preloadUrl(url);
    }

    /**
     * Check if web resources is preloaded.
     *
     * @return true if web resource is preloaded, otherwise return false.
     */
    public boolean isPreloadUrl(String url) {
        mUrl = url;
        return mBrowserManager.isPreloadUrl(url);
    }

    /**
     * Check web resources is load complete.
     *
     * @return true if web resource is load complete, otherwise return false.
     */
    public boolean isLoadComplete(String url) {
        mUrl = url;
        return mBrowserManager.isLoadComplete(url);
    }

    /**
     * Add {@link UrlRouter} to BrowserManager to route different url scheme.
     *
     * @param router router to route given url scheme.
     */
    public void addUrlRouter(String url, UrlRouter router) {
        mUrl = url;
        mBrowserManager.addUrlRouter(url, router);
    }

    /**
     * Load given url by {@link BrowserManager}.
     *
     * @param url given url to preload web resource.
     */
    public void loadUrl(String url) {
        mUrl = url;
        mBrowserManager.loadUrl(url);
    }

    /**
     * Destroy WebView instance.
     */
    public void destroyWebView(String url) {
        mBrowserManager.destroyWebView(url);
    }
}

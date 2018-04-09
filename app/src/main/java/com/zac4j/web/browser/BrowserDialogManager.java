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
 * Class for manage web view in dialog fragment.
 * Created by Zaccc on 2018/1/18.
 */

public class BrowserDialogManager extends BrowserManager {

    private static final String TAG = BrowserDialogManager.class.getSimpleName();

    private AtomicBoolean mHasDialog;
    private BrowserDialogFragment mBrowserDialog;
    private OnLoadStateChangeListener mOnLoadStateChangeListener;

    public BrowserDialogManager(Context appContext) {
        super(appContext);
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
            mBrowserDialog.setOnLifecycleListener(new BrowserDialogFragment.OnLifecycleListener() {
                @Override
                public void onDialogShown(ViewGroup container) {
                    mHasDialog.set(true);
                    assembleWebView(container);
                }

                @Override
                public void onDialogDismiss(ViewGroup container) {
                    mHasDialog.set(false);
                    removeWebView(container);
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

        mOnLoadStateChangeListener = new BrowserManager.OnLoadStateChangeListener() {
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

        registerOnLoadStateChangeListener(mOnLoadStateChangeListener);
    }

    public void showDialogOnLoadComplete(final FragmentManager fragmentManager,
        final Class<? extends BrowserDialogFragment> dialogClass, final Bundle bundle) {

        if (mHasDialog.get()) {
            return;
        }

        mOnLoadStateChangeListener = new BrowserManager.OnLoadStateChangeListener() {
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

        registerOnLoadStateChangeListener(mOnLoadStateChangeListener);
    }

    private BrowserDialogFragment.OnLifecycleListener createBrowserDialogOnLifecycleListener() {
        return new BrowserDialogFragment.OnLifecycleListener() {
            @Override
            public void onDialogShown(ViewGroup container) {
                mHasDialog.set(true);
                assembleWebView(container);
            }

            @Override
            public void onDialogDismiss(ViewGroup container) {
                mHasDialog.set(false);
                removeWebView(container);
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
                unregisterOnLoadStateChangeListener(mOnLoadStateChangeListener);
            }
        }
    }
}

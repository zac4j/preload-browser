package com.zac4j.web.browser;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.webkit.WebView;
import com.zac4j.web.Logger;
import com.zac4j.web.router.UrlRouter;
import com.zac4j.web.Utils;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Helper class to manage Browser.
 * Created by Zaccc on 2018/1/18.
 */

public class BrowserManager {

  private static final String TAG = BrowserManager.class.getSimpleName();

  private static final Object LOCK = new Object();
  private static BrowserManager sInstance;

  private final BrowserDataManager mDataManager;

  private AtomicBoolean mHasDialog;
  private BrowserDialogFragment mBrowserDialog;

  /**
   * Provide BrowserDataManager Singleton
   *
   * @param context It's better to provide application context.
   * @return BrowserDataManager instance.
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

  private BrowserManager(final Context context) {
    mDataManager = new BrowserDataManager(context);
    mHasDialog = new AtomicBoolean(false);
  }

  /**
   * Provide WebView instance.
   *
   * @return WebView instance.
   */
  public WebView getWebView() {
    return mDataManager.getWebView();
  }

  /**
   * Add WebView url intercept spec.
   *
   * @param router url route spec.
   */
  public void addUrlRouter(UrlRouter router) {
    mDataManager.addUrlRouter(router);
  }

  /**
   * Set up WebView default settings.
   */
  public void setupWebViewWithDefaults() {
    mDataManager.setupWebViewWithDefaults(getWebView());
  }

  /**
   * Returns if BrowserManager preload url complete.
   *
   * @return true if preload complete, otherwise false.
   */
  public boolean hasPreloaded() {
    return mDataManager.hasPreloaded();
  }

  /**
   * Preload given url in Browser WebView
   *
   * @param url given url to preload.
   */
  public void preloadUrl(String url) {
    if (Utils.isValidUrl(url)) {
      mDataManager.preloadUrl(url);
    } else {
      throw new IllegalArgumentException("You shouldn't load url with an invalid url");
    }
  }

  /**
   * Load given url in Browser WebView.
   *
   * @param url given url to load.
   */
  public void loadUrl(String url) {
    if (Utils.isValidUrl(url)) {
      mDataManager.loadUrl(url);
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
        public void onDialogShown() {
          mHasDialog.set(true);
          mDataManager.assembleWebView(mBrowserDialog.getBrowserContainer());
        }

        @Override
        public void onDialogDismiss() {
          mHasDialog.set(false);
          mDataManager.removeWebView(mBrowserDialog.getBrowserContainer());
        }
      });
    } catch (InstantiationException | IllegalAccessException e) {
      Logger.e(TAG, e.getMessage());
    }
  }

  /**
   * Close browser dialog
   */
  public void closeDialog() {
    if (mHasDialog.get() && mBrowserDialog != null) {
      mBrowserDialog.dismiss();
    }
  }
}

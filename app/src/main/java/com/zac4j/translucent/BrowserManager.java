package com.zac4j.translucent;

import android.content.Context;
import android.support.v4.app.FragmentManager;
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
  }

  public boolean hasForeLoaded() {
    return mDataManager.hasForeLoaded();
  }

  public void foreLoadUrl(String url) {
    if (Utils.isValidUrl(url)) {
      mDataManager.foreLoadUrl(url);
    } else {
      throw new IllegalArgumentException("Invalid url");
    }
  }

  public void loadUrl(String url) {
    if (Utils.isValidUrl(url)) {
      mDataManager.loadUrl(url);
    } else {
      throw new IllegalArgumentException("Invalid url");
    }
  }

  public void showDialog(FragmentManager fragmentManager) {
    BrowserDialogFragment browserDialog =
        BrowserDialogFragment.newInstance(BrowserDialogFragment.FULLSCREEN);
    browserDialog.show(fragmentManager, TAG);
    mDataManager.assembleWebView(browserDialog.getBrowserContainer());
  }

  public void showOnPageFinished(FragmentManager fragmentManager) {
    mDataManager.showDialogOnPageFinished(BrowserDialogFragment.class, fragmentManager);
  }
}

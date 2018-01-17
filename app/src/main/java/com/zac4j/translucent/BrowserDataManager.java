package com.zac4j.translucent;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A helper class to manage browser container data.
 * Created by Zaccc on 2017/12/7.
 */

public class BrowserDataManager {

  private static final String TAG = BrowserDataManager.class.getSimpleName();

  private static final int TIME_OUT_PROGRESS = 70;
  private static final int TIME_OUT_MILLIS = 3 * 1000;

  private static final Object LOCK = new Object();
  private static BrowserDataManager sInstance;

  private WebView mWebView;

  // 是否使用预加载
  private boolean mIsForeLoad;
  // 是否已预加载
  private AtomicBoolean mHasForeLoaded;

  public static BrowserDataManager getInstance(Context context) {
    Logger.d(TAG, "Getting browser data manager instance");
    if (sInstance == null) {
      synchronized (LOCK) {
        sInstance = new BrowserDataManager(context);
        Logger.d(TAG, "Made new browser data manager");
      }
    }
    return sInstance;
  }

  private BrowserDataManager(Context context) {
    prepareWebView(context);
    mHasForeLoaded = new AtomicBoolean(false);
  }

  /**
   * Load url data before display ui.
   *
   * @param url URL for load.
   */
  public void foreLoadUrl(String url) {

    if (mWebView == null || TextUtils.isEmpty(url)) {
      return;
    }

    mIsForeLoad = true;
    loadUrl(url);
  }

  /**
   * Load remote url
   *
   * @param url remote url
   */
  public void loadUrl(String url) {

    if (mWebView == null || TextUtils.isEmpty(url)) {
      return;
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

    setupWebView(mWebView);

    addBrowserClient(mWebView);
  }

  public void assembleWebView(ViewGroup container) {
    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
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

  public void showDialogAfterLoadData(FragmentManager fragmentManager) {
    if (mHasForeLoaded.get()) {
      showDialog(fragmentManager);
    }
  }

  public void showDialog(FragmentManager fragmentManager) {
    BrowserDialogFragment dialogFragment = BrowserDialogFragment.newInstance();
    dialogFragment.show(fragmentManager);
  }

  /**
   * 是否使用预先加载
   *
   * @return 返回是否预先加载
   */
  public boolean isForeLoad() {
    return mIsForeLoad;
  }

  private void addBrowserClient(WebView webView) {
    try {
      webView.setWebViewClient(new WebViewClient() {

        @Override public void onLoadResource(WebView view, String url) {

          Logger.i(TAG, "onLoadResource url=" + url); // 开始加载
          super.onLoadResource(view, url);
        }

        @Override public boolean shouldOverrideUrlLoading(WebView webview, String url) {

          Logger.i(TAG, "intercept url=" + url);
          // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
          webview.loadUrl(url);
          return true;
        }

        @Override public void onPageStarted(final WebView view, String url, Bitmap favicon) {
          super.onPageStarted(view, url, favicon);

          new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override public void run() {
              if (view.getProgress() < TIME_OUT_PROGRESS) {
                Logger.e(TAG, "Network connection time out ----- current loading progress is: "
                    + view.getProgress());
              }
            }
          }, TIME_OUT_MILLIS);

          Logger.i(TAG, "onPageStarted: ");
        }

        @Override public void onPageFinished(WebView view, String url) {

          String title = view.getTitle(); // 得到网页标题

          Logger.e(TAG, "onPageFinished WebView title=" + title);
        }

        @Override public void onReceivedError(WebView view, int errorCode, String description,
            String failingUrl) {
          Toast.makeText(view.getContext(), "加载错误", Toast.LENGTH_LONG).show();
        }
      });

      webView.setWebChromeClient(new WebChromeClient() {
        @Override public void onProgressChanged(WebView view, int newProgress) {
          super.onProgressChanged(view, newProgress);
          Logger.d(TAG, "onProgressChanged: " + newProgress);
          if (view != null && newProgress == 100 && mIsForeLoad) {
            // Finished fore load.
            mHasForeLoaded.set(true);
          }
        }
      });
    } catch (Exception e) {
      Logger.e(TAG, e.getMessage(), e);
    }
  }

  @SuppressLint("SetJavaScriptEnabled") private static void setupWebView(WebView webView) {
    WebSettings settings = webView.getSettings();
    settings.setJavaScriptEnabled(true);

    settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

    // UI display
    settings.setUseWideViewPort(true);
    settings.setLoadWithOverviewMode(true);
    settings.setBuiltInZoomControls(true);
    settings.setDisplayZoomControls(false);
    settings.setTextZoom(100);
    webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

    // WebView 的访问、存储、缓存设置
    settings.setAllowContentAccess(true);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      settings.setAllowUniversalAccessFromFileURLs(true);
    }

    String cachePath = webView.getContext().getDir("webView_cache", Context.MODE_PRIVATE).getPath();
    settings.setAppCacheEnabled(true);
    settings.setAppCachePath(cachePath);
    settings.setCacheMode(WebSettings.LOAD_DEFAULT);
    settings.setDomStorageEnabled(true);
    settings.setSavePassword(false);
    settings.setDatabaseEnabled(true);
    // Enable record location info
    settings.setGeolocationEnabled(true);

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
}

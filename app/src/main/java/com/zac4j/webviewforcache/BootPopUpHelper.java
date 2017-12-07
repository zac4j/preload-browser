package com.zac4j.webviewforcache;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * A dialog for app start up page
 * Created by Zaccc on 2017/12/7.
 */

public class BootPopUpHelper {

  private static final String TAG = BootPopUpHelper.class.getSimpleName();

  private static final int TIME_OUT_PROGRESS = 70;
  private static final int TIME_OUT_MILLIS = 3 * 1000;

  private static final Object LOCK = new Object();
  private static BootPopUpHelper sInstance;

  private WebView mWebView;
  private boolean mIsPrepared = true;

  public static BootPopUpHelper getInstance() {
    Log.d(TAG, "Getting boot popup view instance");
    if (sInstance == null) {
      synchronized (LOCK) {
        sInstance = new BootPopUpHelper();
        Log.d(TAG, "Made new boot popup view");
      }
    }
    return sInstance;
  }

  public void show(FragmentManager fragmentManager) {
    if (mWebView == null && !mIsPrepared) {
      Log.d(TAG, "WebView data is not well prepared");
      return;
    }
    RedPacketDialogFragment fragment = RedPacketDialogFragment.newInstance();
    fragment.show(fragmentManager, TAG);
  }

  public void dismiss() {
    if (mWebView == null) {
      return;
    }
    mWebView.removeAllViews();
    mWebView.destroy();
    mWebView = null;
  }

  public void loadUrl(Context context, String url) {
    mWebView = new WebView(context.getApplicationContext());

    setupWebView(mWebView);

    prepareWebViewClient(mWebView, url);
  }

  public WebView getWebView() {
    return mWebView;
  }

  private void prepareWebViewClient(WebView webView, String url) {
    try {
      webView.setWebViewClient(new WebViewClient() {

        @Override public void onLoadResource(WebView view, String url) {

          Log.i("tag", "onLoadResource url=" + url); // 开始加载
          super.onLoadResource(view, url);
        }

        @Override public boolean shouldOverrideUrlLoading(WebView webview, String url) {

          Log.i("tag", "intercept url=" + url);
          // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
          webview.loadUrl(url);

          return true;
        }

        @Override public void onPageStarted(final WebView view, String url, Bitmap favicon) {
          super.onPageStarted(view, url, favicon);

          new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override public void run() {
              if (view.getProgress() < TIME_OUT_PROGRESS) {
                Log.e(TAG, "Network connection time out ----- current loading progress is: "
                    + view.getProgress());
                mIsPrepared = false;
              }
            }
          }, TIME_OUT_MILLIS);

          Log.i(TAG, "onPageStarted: ");
        }

        @Override public void onPageFinished(WebView view, String url) {

          String title = view.getTitle(); // 得到网页标题

          Log.e("tag", "onPageFinished WebView title=" + title);
        }

        @Override public void onReceivedError(WebView view, int errorCode, String description,
            String failingUrl) {
          Toast.makeText(view.getContext(), "加载错误", Toast.LENGTH_LONG).show();
        }
      });

      webView.setWebChromeClient(new WebChromeClient() {
        @Override public void onProgressChanged(WebView view, int newProgress) {
          super.onProgressChanged(view, newProgress);
        }
      });

      webView.loadUrl(url);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @SuppressLint("SetJavaScriptEnabled") private void setupWebView(WebView webView) {
    WebSettings settings = webView.getSettings();
    settings.setJavaScriptEnabled(true);

    webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

    // 支持多窗口
    webView.getSettings().setSupportMultipleWindows(true);
    // 开启 DOM storage API 功能
    webView.getSettings().setDomStorageEnabled(true);
    // 开启 Application Caches 功能
    webView.getSettings().setAppCacheEnabled(true);
    // 设置背景透明
    webView.setBackgroundColor(0);
  }
}

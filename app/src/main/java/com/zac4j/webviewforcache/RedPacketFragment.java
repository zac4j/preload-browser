package com.zac4j.webviewforcache;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * A fullscreen dialog fragment
 * Created by Zaccc on 11/20/2017.
 */

public class RedPacketFragment extends DialogFragment {

  private static final String TAG = "RedPacketFragment";

  public static final String URL = "https://dl.app.gtja.com/web/gtjaPopup/hongbao/index.html";

  private static final int SHOW_DIALOG_PROGRESS = 80;
  private static final int TIME_OUT_PROGRESS = 30;
  private static final int TIME_OUT_MILLIS = 3 * 1000;

  public static final String DIALOG_SIZE = "dialog_size";

  public static final int MICRO = 0xaa;
  public static final int FULLSCREEN = 0xbb;

  private WebView webView;
  private int mDialogSize;

  public static RedPacketFragment newInstance(int size) {
    Bundle args = new Bundle();
    RedPacketFragment fragment = new RedPacketFragment();
    args.putInt(DIALOG_SIZE, size);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mDialogSize = getArguments().getInt(DIALOG_SIZE, 0);
  }

  @SuppressLint("SetJavaScriptEnabled") @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_dialog_fullscreen, container, false);

    webView = view.findViewById(R.id.webview);

    webView.setWebChromeClient(new WebChromeClient());

    WebSettings settings = webView.getSettings();
    settings.setJavaScriptEnabled(true);

    webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

    // webView.getSettings().setBlockNetworkImage(true);// 把图片加载放在最后来加载渲染
    // webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
    // 支持多窗口
    webView.getSettings().setSupportMultipleWindows(true);
    // 开启 DOM storage API 功能
    webView.getSettings().setDomStorageEnabled(true);
    // 开启 Application Caches 功能
    webView.getSettings().setAppCacheEnabled(true);

    webView.setBackgroundColor(0);

    prepareImage(webView);

    return view;
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    final Dialog dialog = super.onCreateDialog(savedInstanceState);

    if (dialog.getWindow() == null) {
      return super.onCreateDialog(savedInstanceState);
    }

    dialog.getWindow().getAttributes().windowAnimations = R.style.RedPacketAnimation;
    return dialog;
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {

    // Dialog container
    Window window = getDialog().getWindow();

    if (mDialogSize == 0 || window == null) {
      super.onActivityCreated(savedInstanceState);
      return;
    }

    if (mDialogSize == FULLSCREEN) {
      window.requestFeature(Window.FEATURE_NO_TITLE);
      super.onActivityCreated(savedInstanceState);
    } else if (mDialogSize == MICRO) {
      super.onActivityCreated(savedInstanceState);
    }

    WindowManager.LayoutParams lp = window.getAttributes();
    lp.dimAmount = 0f;
    window.setAttributes(lp);
    window.setBackgroundDrawable(new ColorDrawable(0x00000000));
    window.setLayout(1, 1);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    webView.removeAllViews();
    webView.destroy();
  }

  private void prepareImage(final WebView webView) {
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

        @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
          super.onPageStarted(view, url, favicon);

          Log.i(TAG, "onPageStarted: ");

          new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override public void run() {
              if (webView.getProgress() < TIME_OUT_PROGRESS) {
                Log.e(TAG, "RedPacket dialog connection time out ----- current progress is: "
                    + webView.getProgress());
                if (getDialog() != null) {
                  getDialog().dismiss();
                }
              }
            }
          }, TIME_OUT_MILLIS);
        }

        @Override public void onPageFinished(WebView view, String url) {

          String title = view.getTitle(); // 得到网页标题

          Log.e("tag", "onPageFinished WebView title=" + title);
        }

        @Override public void onReceivedError(WebView view, int errorCode, String description,
            String failingUrl) {
          Toast.makeText(getActivity(), "加载错误", Toast.LENGTH_LONG).show();
        }
      });

      webView.setWebChromeClient(new WebChromeClient() {
        @Override public void onProgressChanged(WebView view, int newProgress) {
          super.onProgressChanged(view, newProgress);
          if (newProgress >= SHOW_DIALOG_PROGRESS && mDialogSize == FULLSCREEN) {
            getDialog().getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
          }
        }
      });

      webView.loadUrl(URL);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

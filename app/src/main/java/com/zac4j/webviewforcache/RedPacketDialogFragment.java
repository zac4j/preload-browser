package com.zac4j.webviewforcache;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * A fullscreen dialog fragment
 * Created by Zaccc on 11/20/2017.
 */

public class RedPacketDialogFragment extends DialogFragment {

  public static RedPacketDialogFragment newInstance() {
    return new RedPacketDialogFragment();
  }

  @SuppressLint("SetJavaScriptEnabled") @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_dialog_fullscreen, container, false);
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    Window window = getDialog().getWindow();

    if (window == null) {
      super.onActivityCreated(savedInstanceState);
      return;
    }

    window.requestFeature(Window.FEATURE_NO_TITLE);
    super.onActivityCreated(savedInstanceState);
    WindowManager.LayoutParams lp = window.getAttributes();
    lp.dimAmount = 0f;
    window.setAttributes(lp);
    // 占满全屏
    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT);
    window.setBackgroundDrawable(new ColorDrawable(0x00000000));
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    FrameLayout container = view.findViewById(R.id.container);
    WebView webView = BootPopUpHelper.getInstance().getWebView();
    if (webView != null) {
      container.addView(webView);
    }
  }
}

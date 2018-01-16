package com.zac4j.webviewforcache;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;

/**
 * A fullscreen dialog fragment
 * Created by Zaccc on 11/20/2017.
 */

public class TranslucentWebDialogFragment extends DialogFragment {

  private static final String TAG = TranslucentWebDialogFragment.class.getSimpleName();
  private FrameLayout mBrowserContainer;

  public static TranslucentWebDialogFragment newInstance() {
    return new TranslucentWebDialogFragment();
  }

  @SuppressLint("SetJavaScriptEnabled") @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_dialog_fullscreen, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mBrowserContainer = view.findViewById(R.id.container);
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
}

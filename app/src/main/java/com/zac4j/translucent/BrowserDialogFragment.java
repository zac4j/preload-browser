package com.zac4j.translucent;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * A fullscreen webView dialog fragment
 * Created by Zaccc on 11/20/2017.
 */

public class BrowserDialogFragment extends DialogFragment {

  private static final String TAG = BrowserDialogFragment.class.getSimpleName();

  public static BrowserDialogFragment newInstance() {
    return new BrowserDialogFragment();
  }

  @SuppressLint("SetJavaScriptEnabled") @Nullable @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_dialog_fullscreen, container, false);
  }

  @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    FrameLayout browserContainer = view.findViewById(R.id.container);
    BrowserDataManager.getInstance(getContext().getApplicationContext())
        .assembleWebView(browserContainer);
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

  public void show(FragmentManager fragmentManager) {
    super.show(fragmentManager, TAG);
  }
}

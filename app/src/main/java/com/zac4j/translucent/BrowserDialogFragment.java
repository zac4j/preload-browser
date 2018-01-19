package com.zac4j.translucent;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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

  public interface OnLifecycleListener {
    void onDialogShown();

    void onDialogDismiss();
  }

  private OnLifecycleListener mLifecycleListener;

  public void setOnLifecycleListener(OnLifecycleListener listener) {
    mLifecycleListener = listener;
  }

  public static BrowserDialogFragment newInstance() {
    return new BrowserDialogFragment();
  }

  @SuppressLint("SetJavaScriptEnabled")
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    Logger.d(TAG, "Dialog onCreateView");
    return inflater.inflate(R.layout.fragment_dialog_fullscreen, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Logger.d(TAG, "Dialog onViewCreated");
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    Logger.d(TAG, "Dialog onActivityCreated");
    // 获取屏幕Window 对象
    super.onActivityCreated(savedInstanceState);

    Window window = getDialog().getWindow();

    if (window == null) {
      return;
    }

    // 全屏展示Dialog
    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT);

    // 去除Dialog 灰色底色
    WindowManager.LayoutParams params = window.getAttributes();
    params.dimAmount = 0.0f;
    window.setAttributes(params);
    // 底色为全透明
    window.setBackgroundDrawable(new ColorDrawable(0x00000000));

    mLifecycleListener.onDialogShown();
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    mLifecycleListener.onDialogDismiss();
  }

  public ViewGroup getBrowserContainer() {
    if (getView() != null) {
      return getView().findViewById(R.id.container);
    } else {
      throw new IllegalStateException(
          "You should invoke getBrowserContainer() in OnLifecycleListener.onDialogShown()");
    }
  }
}

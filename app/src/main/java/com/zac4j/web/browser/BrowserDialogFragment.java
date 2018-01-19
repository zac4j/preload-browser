package com.zac4j.web.browser;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ViewGroup;
import com.zac4j.web.Logger;

/**
 * A fullscreen webView dialog fragment
 * Created by Zaccc on 11/20/2017.
 */

public abstract class BrowserDialogFragment extends DialogFragment {

  private static final String TAG = BrowserDialogFragment.class.getSimpleName();

  public interface OnLifecycleListener {
    void onDialogShown();

    void onDialogDismiss();
  }

  private OnLifecycleListener mLifecycleListener;

  public void setOnLifecycleListener(OnLifecycleListener listener) {
    mLifecycleListener = listener;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    Logger.d(TAG, "Dialog onActivityCreated");
    super.onActivityCreated(savedInstanceState);

    mLifecycleListener.onDialogShown();
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);

    mLifecycleListener.onDialogDismiss();
  }

  public abstract ViewGroup getBrowserContainer();

}

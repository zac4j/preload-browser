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
    private LifecycleListener mLifecycleListener;

    public void setLifecycleListener(LifecycleListener listener) {
        mLifecycleListener = listener;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Logger.d(TAG, "Dialog onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        mLifecycleListener.onDialogShown(provideBrowserContainer());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        mLifecycleListener.onDialogDismiss(provideBrowserContainer());
    }

    public abstract ViewGroup provideBrowserContainer();

    public interface LifecycleListener {
        void onDialogShown(ViewGroup container);

        void onDialogDismiss(ViewGroup container);
    }
}

package com.zac4j.web.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import com.zac4j.web.Logger;
import com.zac4j.web.R;
import com.zac4j.web.browser.BrowserDialogFragment;

/**
 * A fullscreen WebView dialog fragment
 * Created by Zaccc on 11/20/2017.
 */

public class RedPacketDialogFragment extends BrowserDialogFragment {

    private static final String TAG = RedPacketDialogFragment.class.getSimpleName();

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        Logger.d(TAG, "Dialog onCreateView");
        return inflater.inflate(R.layout.fragment_dialog_fullscreen, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Logger.d(TAG, "Dialog onActivityCreated");

        // 获取屏幕Window 对象
        Window window = getDialog().getWindow();

        if (window == null) {
            super.onActivityCreated(savedInstanceState);
            return;
        }

        window.requestFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);

        // 全屏展示Dialog
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT);

        // 去除Dialog 灰色底色
        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = 0.0f;
        window.setAttributes(params);
        // 底色为全透明
        window.setBackgroundDrawable(new ColorDrawable(0x00000000));
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public ViewGroup getBrowserContainer() {
        if (getView() != null) {
            return getView().findViewById(R.id.container);
        } else {
            throw new IllegalStateException(
                "You should invoke getBrowserContainer() in OnLifecycleListener.onDialogShown()");
        }
    }
}

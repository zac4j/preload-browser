package com.zac4j.web.browser;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.lang.ref.WeakReference;

/**
 * A handler for manage browser tasks.
 * Created by Zaccc on 2018/1/18.
 */

public class BrowserHandler extends Handler {

    private WeakReference<Context> mWeakRef;

    public BrowserHandler(Context context) {
        mWeakRef = new WeakReference<>(context);
    }

    public BrowserHandler(Context context, Looper looper) {
        super(looper);
        mWeakRef = new WeakReference<>(context);
    }

    @Override
    public void handleMessage(Message msg) {
        if (mWeakRef == null) {
            throw new IllegalArgumentException("Handler reference can not be null!");
        } else {
            super.handleMessage(msg);
        }
    }
}

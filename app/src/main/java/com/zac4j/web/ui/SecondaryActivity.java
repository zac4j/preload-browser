package com.zac4j.web.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.webkit.WebView;
import android.widget.Toast;
import com.zac4j.web.Logger;
import com.zac4j.web.R;
import com.zac4j.web.Utils;
import com.zac4j.web.browser.DialogBrowserLoader;
import com.zac4j.web.Scheme;
import com.zac4j.web.router.UrlRouter;
import com.zac4j.web.ui.dialog.GameDialogFragment;

public class SecondaryActivity extends AppCompatActivity {

    private static final String TAG = SecondaryActivity.class.getSimpleName();
    private DialogBrowserLoader mBrowserManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState,
        @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_secondary);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.d(TAG, "onStart");

        mBrowserManager = DialogBrowserLoader.getInstance(getApplicationContext());
        String url = Utils.provideUrl();
        // Verify if browser manager preload data complete.
        if (mBrowserManager.isPreloadUrl(url)) {
            if (mBrowserManager.isLoadComplete(url)) {
                Logger.d(TAG, "Web page has preload complete -> go!");
                // add intercept scheme in the WebViewClient::shouldOverrideUrlLoading url route specification.
                mBrowserManager.addUrlRouter(url, new UrlRouter() {
                    @Override
                    public boolean route(WebView webView, String scheme) {

                        if (TextUtils.isEmpty(scheme)) {
                            return false;
                        }

                        if (encodeScheme(scheme).startsWith(Scheme.OPEN_RED_PACKET)) {
                            Toast.makeText(SecondaryActivity.this, "Nice, You open this RedPacket!",
                                Toast.LENGTH_SHORT).show();
                            return true;
                        } else if (encodeScheme(scheme).startsWith(Scheme.CLOSE_RED_PACKET)) {
                            mBrowserManager.closeDialog();
                            return true;
                        }

                        return false;
                    }
                });
                mBrowserManager.showDialog(getSupportFragmentManager(), GameDialogFragment.class);
            } else {
                Logger.d(TAG, "Web page is preload and haven't load complete.");
                mBrowserManager.showDialogOnLoadComplete(getSupportFragmentManager(),
                    GameDialogFragment.class);
            }
        } else {
            Logger.d(TAG, "Web page haven't load yet.");
            mBrowserManager.loadUrl(url);
            mBrowserManager.showDialogOnLoadComplete(getSupportFragmentManager(),
                GameDialogFragment.class);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBrowserManager.closeDialog();
    }

    private String encodeScheme(String scheme) {
        return Utils.generateMD5(scheme).toUpperCase();
    }
}

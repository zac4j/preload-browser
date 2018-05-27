package com.zac4j.web.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Toast;
import com.zac4j.web.Logger;
import com.zac4j.web.R;
import com.zac4j.web.Utils;
import com.zac4j.web.browser.BrowserDialogManager;
import com.zac4j.web.browser.Scheme;
import com.zac4j.web.router.UrlRouter;
import com.zac4j.web.ui.dialog.RedPacketDialogFragment;
import java.io.UnsupportedEncodingException;

public class SecondaryActivity extends AppCompatActivity {

    private static final String TAG = SecondaryActivity.class.getSimpleName();
    private BrowserDialogManager mBrowserManager;

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

        mBrowserManager = BrowserDialogManager.getInstance(getApplicationContext());

        // Verify if browser manager preload data complete.
        if (mBrowserManager.isPreload()) {
            if (mBrowserManager.isLoadComplete()) {
                Logger.d(TAG, "Web page has preload complete");
                // add intercept scheme in the WebViewClient::shouldOverrideUrlLoading url route specification.
                mBrowserManager.addUrlRouter(new UrlRouter() {
                    @Override
                    public boolean route(String scheme) {

                        if (TextUtils.isEmpty(scheme)) {
                            return false;
                        }

                        if (Utils.generateMD5(scheme)
                            .toUpperCase()
                            .startsWith(Scheme.OPEN_RED_PACKET)) {
                            Toast.makeText(SecondaryActivity.this, "Nice, You open this RedPacket!",
                                Toast.LENGTH_SHORT).show();
                            return true;
                        } else if (Utils.generateMD5(scheme)
                            .toUpperCase()
                            .startsWith(Scheme.CLOSE_RED_PACKET)) {
                            mBrowserManager.closeDialog();
                            return true;
                        }

                        return false;
                    }
                });
                mBrowserManager.showDialog(getSupportFragmentManager(),
                    RedPacketDialogFragment.class);
            } else {
                mBrowserManager.showDialogOnLoadComplete(getSupportFragmentManager(),
                    RedPacketDialogFragment.class);
            }
        } else {
            Logger.d(TAG, "Web page haven't load yet");
            byte[] buffer = Base64.decode(Scheme.URL, Base64.DEFAULT);
            try {
                String url = new String(buffer, "UTF-8");
                mBrowserManager.loadUrl(url);
                mBrowserManager.showDialogOnLoadComplete(getSupportFragmentManager(),
                    RedPacketDialogFragment.class);
            } catch (UnsupportedEncodingException e) {
                Logger.e(TAG, e.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBrowserManager.closeDialog();
    }
}

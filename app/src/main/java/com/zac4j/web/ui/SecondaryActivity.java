package com.zac4j.web.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;
import com.zac4j.web.Logger;
import com.zac4j.web.R;
import com.zac4j.web.browser.Browser;
import com.zac4j.web.browser.BrowserManager;
import com.zac4j.web.router.UrlRouter;

public class SecondaryActivity extends AppCompatActivity {

    private static final String TAG = SecondaryActivity.class.getSimpleName();
    private BrowserManager mBrowserManager;

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

        mBrowserManager = BrowserManager.getInstance(getApplicationContext());

        // Verify if browser manager preload data complete.
        if (mBrowserManager.isPreload()) {
            if (mBrowserManager.isLoadComplete()) {
                Logger.d(TAG, "Web page has preload complete");
                // Step three: add intercept scheme in the WebViewClient::shouldOverrideUrlLoading url route specification.
                mBrowserManager.addUrlRouter(new UrlRouter() {
                    @Override
                    public boolean route(String scheme) {

                        if (TextUtils.isEmpty(scheme)) {
                            return false;
                        }

                        if (scheme.startsWith(Browser.OPEN_RED_PACKET)) {
                            Toast.makeText(SecondaryActivity.this, "Nice, You open this RedPacket!",
                                Toast.LENGTH_SHORT).show();
                            return true;
                        } else if (scheme.startsWith(Browser.CLOSE_RED_PACKET)) {
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
            mBrowserManager.loadUrl(Browser.URL);
            // Don't forget setup WebView settings
            mBrowserManager.setupWebViewWithDefaults();
            mBrowserManager.showDialogOnLoadComplete(getSupportFragmentManager(),
                RedPacketDialogFragment.class);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBrowserManager.closeDialog();
    }
}

package com.zac4j.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Base64;
import android.util.Patterns;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import com.zac4j.web.browser.Scheme;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final String TAG = "Utils";

    /**
     * This is used to check the network is available or not.
     *
     * @param context context to fetch system service.
     * @return true if network is available, false otherwise.
     */
    public static boolean isNetworkAvailable(Context context) {
        boolean status = false;
        try {
            ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                    status = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;
    }

    /**
     * Provide remote url.
     *
     * @return remote url.
     */
    public static String provideUrl() {
        String result = "";
        byte[] buffer = Base64.decode(Scheme.URL, Base64.DEFAULT);
        try {
            result = new String(buffer, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Logger.e(TAG, e.getMessage());
        }
        return result;
    }

    /**
     * This is used to check the given URL is valid or not.
     *
     * @param url url to check if is valid.
     * @return true if url is valid, false otherwise.
     */
    public static boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }

    /**
     * Generate the md5 of given string text.
     *
     * @param text string text to generate md5.
     * @return md5 of given text.
     */
    public static String generateMD5(final String text) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(text.getBytes());
            final byte[] buffer = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (byte b : buffer) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString().toLowerCase();
        } catch (Exception e) {
            Logger.e("Utils", e.getMessage());
            return "";
        }
    }

    /**
     * Gets the Base64-encoded string of a local asset file (typically a Javascript or HTML file).
     * @param context Activity context.
     * @param filePath Local file path relative to the main/src directory.
     * @return A Base64 encoded string of the file contents.
     * @throws IOException Typically if a file cannot be found or read in.
     */
    public static String getContentFromAsset(Context context, String filePath) throws IOException {
        InputStream is = context.getAssets().open(filePath);
        byte[] buffer = new byte[is.available()];
        is.read(buffer);
        is.close();
        return Base64.encodeToString(buffer, Base64.NO_WRAP);
    }

    /**
     * Executes a given runnable on the main thread.
     *
     * @param context Activity context.
     * @param runnable A runnable to execute on the main thread.
     */
    public static void runOnMainThread(Context context, Runnable runnable) {
        Handler handler = new Handler(context.getMainLooper());
        handler.post(runnable);
    }

    /**
     * Configures basic settings of the webView (Javascript enabled, DOM storage enabled,
     * database enabled).
     *
     * @param webView The shared webView.
     */
    @SuppressLint("SetJavaScriptEnabled")
    public static void configWebViewDefaults(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);

        webView.setWebChromeClient(new WebChromeClient());
    }

    /**
     * Sets the WebView's width/height layout params to MATCH_PARENT.
     *
     * @param webView The shared webView.
     */
    public static void setWebViewLayoutParams(WebView webView) {
        FrameLayout.LayoutParams params =
            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(params);
    }
}


package com.zac4j.web;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Patterns;
import java.io.Closeable;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

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

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                Log.d("Utils", "close() called failed with: )" + e.getMessage());
            }
        }
    }
}


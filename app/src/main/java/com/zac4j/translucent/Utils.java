package com.zac4j.translucent;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Patterns;
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
        if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) status = true;
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
}

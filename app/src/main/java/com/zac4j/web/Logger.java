package com.zac4j.web;

import android.util.Log;

/**
 * project logger
 * Created by Zac on 2018/1/16.
 */

public class Logger {

  private static final String TAG = "Logger";

  public static void d(String tag, String message) {
    if (BuildConfig.DEBUG) {
      Log.d(tag, message);
    }
  }

  public static void d(String tag, String message, Throwable throwable) {
    if (BuildConfig.DEBUG) {
      Log.d(tag, message, throwable);
    }
  }

  public static void e(String tag, String message) {
    if (BuildConfig.DEBUG) {
      Log.e(tag, message);
    }
  }

  public static void e(String tag, String message, Throwable throwable) {
    if (BuildConfig.DEBUG) {
      Log.e(tag, message, throwable);
    }
  }

  public static void i(String tag, String message) {
    if (BuildConfig.DEBUG) {
      Log.i(tag, message);
    }
  }

  public static void i(String tag, String message, Throwable throwable) {
    if (BuildConfig.DEBUG) {
      Log.i(tag, message, throwable);
    }
  }
}

package com.zac4j.web.loader;

import com.zac4j.web.Logger;
import com.zac4j.web.network.LoggingInterceptor;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

/**
 * Created by Zaccc on 2018/4/20.
 */
public class BrowserCacheManager {

    private static final String TAG = "WebPageLoadManager";

    private static BrowserCacheManager sInstance;
    private static final Object LOCK = new Object();
    private Map<String, String> mCachedPageMapper;

    private BrowserCacheManager() {
        if (mCachedPageMapper == null) {
            mCachedPageMapper = new HashMap<>();
        }
    }

    public static BrowserCacheManager getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new BrowserCacheManager();
            }
        }
        return sInstance;
    }

    public void loadUrl(final String remoteUrl, final String cacheLocation) {
        Request request = new Request.Builder().url(remoteUrl).get().build();

        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(new LoggingInterceptor())
            .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.e(TAG, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response == null) {
                    return;
                }

                Logger.d(TAG,
                    "Remote url " + remoteUrl + " load finished,  cached in: " + cacheLocation);

                File file = new File(cacheLocation);
                BufferedSink sink = Okio.buffer(Okio.sink(file));
                sink.writeAll(response.body().source());
                sink.close();

                mCachedPageMapper.put(remoteUrl, cacheLocation);
            }
        });
    }

    public String getCacheLocation(String url) {
        if (mCachedPageMapper != null && mCachedPageMapper.containsKey(url)) {
            return mCachedPageMapper.get(url);
        } else {
            return "";
        }
    }
}
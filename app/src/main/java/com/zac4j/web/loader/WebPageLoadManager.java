package com.zac4j.web.loader;

import com.zac4j.web.Logger;
import com.zac4j.web.Utils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Zaccc on 2018/4/20.
 */
public class WebPageLoadManager {

    private static final String TAG = "WebPageLoadManager";

    private static WebPageLoadManager sInstance;
    private static final Object LOCK = new Object();
    private Map<String, InputStream> mCachedPageMapper;

    private WebPageLoadManager() {
        if (mCachedPageMapper == null) {
            mCachedPageMapper = new HashMap<>();
        }
    }

    public static WebPageLoadManager getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new WebPageLoadManager();
            }
        }
        return sInstance;
    }

    public void loadUrl(final String remoteUrl, final File cacheFile) {
        Request request = new Request.Builder().header("Accept", "text/html")
            .header("Accept-Encoding", "gzip")
            .header("Accept-Language", "zh-CN,zh;")
            .url(remoteUrl)
            .get()
            .build();

        OkHttpClient client = new OkHttpClient.Builder().followRedirects(false)
            .followSslRedirects(false)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.e(TAG, "load url failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response == null || response.body() == null) {
                    return;
                }

                InputStream byteStream = null;
                try {
                    byteStream = response.body().byteStream();
                    String contentEncoding = response.headers().get("Content-Encoding");
                    InputStream is;
                    if ("gzip".equals(contentEncoding)) {
                        is = new BufferedInputStream(new GZIPInputStream(byteStream));
                    } else {
                        is = new BufferedInputStream(byteStream);
                    }
                    mCachedPageMapper.put(remoteUrl, is);
                } catch (IOException e) {
                    Logger.e(TAG, e.getMessage());
                } finally {
                    Utils.close(byteStream);
                }
            }
        });
    }

    public InputStream getCacheStream(String url) {
        if (mCachedPageMapper != null && mCachedPageMapper.containsKey(url)) {
            return mCachedPageMapper.get(url);
        } else {
            return null;
        }
    }

    public boolean containsUrl(String url) {
        return mCachedPageMapper != null && mCachedPageMapper.containsKey(url);
    }
}

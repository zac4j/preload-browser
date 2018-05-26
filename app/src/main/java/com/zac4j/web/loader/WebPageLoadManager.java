package com.zac4j.web.loader;

import com.zac4j.web.Logger;
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
import okio.BufferedSink;
import okio.Okio;

/**
 * Created by Zaccc on 2018/4/20.
 */
public class WebPageLoadManager {

    private static final String TAG = "WebPageLoadManager";

    private static WebPageLoadManager sInstance;
    private static final Object LOCK = new Object();
    private Map<String, String> mCachedPageMapper;

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

    public void loadUrl(final String remoteUrl, final String destinationDir) {
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
                Logger.e(TAG, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response == null
                    || response.body() == null
                    || response.body().source() == null) {
                    return;
                }

                InputStream is = response.body().byteStream();
                InputStream sourceStream;
                if ("gzip".equalsIgnoreCase(response.header("Content-Encoding"))) {
                    sourceStream = new BufferedInputStream(new GZIPInputStream(is));
                } else {
                    sourceStream = new BufferedInputStream(is);
                }

                File file = new File(destinationDir);
                BufferedSink sink = Okio.buffer(Okio.sink(file));
                sink.writeAll(response.body().source());
                sink.close();

                response.body().source();

                mCachedPageMapper.put(remoteUrl, destinationDir);
            }
        });
    }

    public String getUrlCache(String url) {
        if (mCachedPageMapper != null && mCachedPageMapper.containsKey(url)) {
            return mCachedPageMapper.get(url);
        } else {
            return "";
        }
    }
}

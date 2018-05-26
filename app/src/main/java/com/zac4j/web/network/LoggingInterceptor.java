package com.zac4j.web.network;

import com.zac4j.web.Logger;
import java.io.IOException;
import java.util.Locale;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zac on 2018/5/26.
 * Description:Http request logging interceptor
 */
public class LoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        Logger.i(String.format("Sending request %s on %s%n%s", request.url(), chain.connection(),
            request.headers()));

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        Logger.i(String.format(Locale.getDefault(), "Received response for %s in %.1fms%n%s",
            response.request().url(), (t2 - t1) / 1e6d, response.headers()));

        return response;
    }
}

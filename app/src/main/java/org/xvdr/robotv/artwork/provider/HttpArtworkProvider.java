package org.xvdr.robotv.artwork.provider;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

abstract class HttpArtworkProvider extends ArtworkProvider {

    private final static String TAG = "HttpArtworkProvider";

    private int mDelayAfterRequestMs = 0;
    private OkHttpClient client;

    HttpArtworkProvider() {
        this(5);
    }

    HttpArtworkProvider(int concurrentConnections) {
        client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(concurrentConnections, 5, TimeUnit.MINUTES))
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .build();
    }

    void setDelayAfterRequest(int delayMs) {
        mDelayAfterRequestMs = delayMs;
    }

    boolean checkUrlExists(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        int status = response.code();

        if(status == 200) {
            return true;
        }

        Log.d(TAG, "failed (status: " + status + ")");
        return false;
    }

    String getResponseFromServer(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        if(mDelayAfterRequestMs > 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(mDelayAfterRequestMs);
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
        }

        return response.body().string();
    }
}

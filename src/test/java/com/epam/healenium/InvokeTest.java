package com.epam.healenium;

import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Slf4j
public class InvokeTest {

    @Test
    @Ignore
    public void check() {
        try {
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://localhost:7878/healenium/results").newBuilder();
            urlBuilder.addQueryParameter("locator", "//div[@title='inner']");
            String url = urlBuilder.build().toString();

            Request request = new Request.Builder().url(url).build();
            Response response = new OkHttpClient().newCall(request).execute();
            String result = response.body().string();
            Assert.assertNotNull(result);
        } catch (Exception ex) {}
    }
}

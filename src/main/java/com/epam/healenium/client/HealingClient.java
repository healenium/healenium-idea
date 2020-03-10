package com.epam.healenium.client;

import com.epam.healenium.model.Locator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
public class HealingClient {

    private static final ObjectMapper mapper = new ObjectMapper();

    public List<Locator> makeCall(String selector, String className, String methodName){
        try{
            //TODO: Need add config UI section
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://localhost:7878/healenium/results").newBuilder();
            urlBuilder.addQueryParameter("locator", selector);
            urlBuilder.addQueryParameter("className", className);
            urlBuilder.addQueryParameter("methodName", methodName);
            String url = urlBuilder.build().toString();

            Request request = new Request.Builder().url(url).build();
            Response response = new OkHttpClient().newCall(request).execute();
            String result = response.body().string();
            return mapper.readValue(result, new TypeReference<List<Locator>>(){});
        } catch (Exception ex){
            return Collections.emptyList();
        }
    }

}

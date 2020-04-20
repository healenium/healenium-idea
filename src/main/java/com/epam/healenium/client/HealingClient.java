package com.epam.healenium.client;

import com.epam.healenium.model.HealingDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Collections;
import java.util.Set;

@Slf4j
public class HealingClient {

    private static final ObjectMapper mapper = new ObjectMapper();

    private String host = "localhost";
    private Integer port = 7878;

    public HealingClient() {
    }

    public HealingClient(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public Set<HealingDto> makeCall(String selector, String className){
        try{
            //TODO: Need add config UI section
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://"+ host+":"+port+"/healenium/healing").newBuilder();
            urlBuilder.addQueryParameter("locator", selector);
            urlBuilder.addQueryParameter("className", className);
            String url = urlBuilder.build().toString();

            Request request = new Request.Builder().url(url).build();
            Response response = new OkHttpClient().newCall(request).execute();
            String result = response.body().string();
            return mapper.readValue(result, new TypeReference<Set<HealingDto>>(){});
        } catch (Exception ex){
            return Collections.emptySet();
        }
    }

}

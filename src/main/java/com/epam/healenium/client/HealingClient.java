package com.epam.healenium.client;

import com.epam.healenium.model.HealingDto;
import com.epam.healenium.settings.HealeniumSettingsState;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Collections;
import java.util.Set;

@Slf4j
public class HealingClient {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public HealingClient() {
    }

    public Set<HealingDto> makeCall(String selector, String className){
        try{
            String serverUrl = HealeniumSettingsState.getInstance().getServerUrl();
            HttpUrl.Builder urlBuilder = HttpUrl.parse(serverUrl + "/healenium/healing").newBuilder();
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

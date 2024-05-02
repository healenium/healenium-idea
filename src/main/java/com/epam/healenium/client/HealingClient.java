package com.epam.healenium.client;

import com.epam.healenium.model.HealingDto;
import com.epam.healenium.settings.HealeniumSettingsState;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;

@Slf4j
public class HealingClient {

    private static final ObjectMapper mapper = new ObjectMapper();

    public HealingClient() {
    }

    public Set<HealingDto> makeCall(String selector, String className){
        try{
            String serverUrl = HealeniumSettingsState.getInstance().getServerUrl();
            String spec = serverUrl + "/healenium/healing" +
                    "?locator=" + URLEncoder.encode(selector, StandardCharsets.UTF_8);

            if (className != null){
                spec += "&className=" + URLEncoder.encode(className, StandardCharsets.UTF_8);
            }

            URL url = new URL(spec);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + connection.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (connection.getInputStream())));

            String line;
            StringBuilder result = new StringBuilder();
            while ((line = br.readLine()) != null) {
                result.append(line);
            }

            connection.disconnect();

            return mapper.readValue(result.toString(), new TypeReference<Set<HealingDto>>() {});
        } catch (Exception ex){
            return Collections.emptySet();
        }
    }
}

package com.epam.healenium.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.time.LocalDateTime;

public class CustomDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException {
        ArrayNode node = parser.getCodec().readTree(parser);
        int year = (Integer) node.get(0).numberValue();
        int month = (Integer) node.get(1).numberValue();
        int day = (Integer) node.get(2).numberValue();
        int hour = (Integer) node.get(3).numberValue();
        int minute = (Integer) node.get(4).numberValue();
        int second = (Integer) node.get(5).numberValue();
        int nano = (Integer) node.get(6).numberValue();
        return LocalDateTime.of(year, month, day, hour, minute, second, nano);
    }
}

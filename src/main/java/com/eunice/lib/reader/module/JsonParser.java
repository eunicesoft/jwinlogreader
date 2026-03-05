package com.eunice.lib.reader.module;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;

public class JsonParser {


    private static final XmlMapper XML_MAPPER = new XmlMapper();
    private static final ObjectMapper MAPPER = new ObjectMapper();


    public String normalize(String xml) throws IOException {

        if (xml == null) return null;

        JsonNode root = XML_MAPPER.readTree(xml.getBytes());
        ObjectNode normalized = MAPPER.createObjectNode();

        JsonNode system = root.path("System");
        ObjectNode systemNode = normalized.putObject("system");

        systemNode.put("provider", system.path("Provider").path("Name").asText());
        systemNode.put("event_id", system.path("EventID").asInt());
        systemNode.put("channel", system.path("Channel").asText());
        systemNode.put("computer", system.path("Computer").asText());
        systemNode.put(
                "time_created",
                system.path("TimeCreated").path("SystemTime").asText()
        );

        ObjectNode eventDataNode = normalized.putObject("event_data");

        JsonNode dataArray = root.path("EventData").path("Data");
        if (dataArray.isArray()) {
            for (JsonNode item : dataArray) {
                String name = item.path("Name").asText();
                String value = item.path("").asText();


                if ("Hashes".equals(name)) {
                    eventDataNode.set("Hashes", parseHashes(value));
                } else {
                    eventDataNode.put(name, value);
                }
            }
        }

        return MAPPER.copy().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(normalized);
    }

    private static ObjectNode parseHashes(String hashString) {
        ObjectNode hashes = MAPPER.createObjectNode();

        String[] parts = hashString.split(",");
        for (String part : parts) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2) {
                hashes.put(kv[0], kv[1]);
            }
        }
        return hashes;
    }



}

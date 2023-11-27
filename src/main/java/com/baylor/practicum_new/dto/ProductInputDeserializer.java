package com.baylor.practicum_new.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ProductInputDeserializer extends JsonDeserializer<BulkUploadDTO.ProductInput> {

    @Override
    public BulkUploadDTO.ProductInput deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        JsonNode node = jp.getCodec().readTree(jp);
        String productName = node.get("productName").asText();
        String description = node.get("description").asText();
        Set<Long> categoryIds = new HashSet<>();
        if (node.has("categoryIds")) {
            Iterator<JsonNode> elements = node.get("categoryIds").elements();
            while (elements.hasNext()) {
                categoryIds.add(elements.next().asLong());
            }
        }

        return new BulkUploadDTO.ProductInput(productName, description, categoryIds);
    }
}

package com.github.nez.utils;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JSONPayload {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSONPayload.class);

    private JSONObject jsonObject;

    public JSONPayload addJSON(String filePath) throws IOException {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        LOGGER.debug("Loading JSON payload from: {}", filePath);

        Path jsonPath = Paths.get(filePath);
        String json = new String(Files.readAllBytes(jsonPath));
        this.jsonObject = new JSONObject(json);

        LOGGER.debug("JSON payload loaded successfully");

        return this;
    }

    public JSONObject create() {
        if (jsonObject == null) {
            throw new IllegalStateException("JSON object has not been initialized. Call addJSON() first.");
        }
        return this.jsonObject;
    }
}
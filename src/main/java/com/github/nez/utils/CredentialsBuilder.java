package com.github.nez.utils;

import com.adobe.pdfservices.operation.auth.ServicePrincipalCredentials;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CredentialsBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(CredentialsBuilder.class);

    private String filePath;

    public ServicePrincipalCredentials createCredentials() throws IOException {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalStateException("File path must be provided before creating credentials");
        }

        LOGGER.debug("Loading credentials from: {}", filePath);

        String content = new String(Files.readAllBytes(Paths.get(this.filePath)));
        JSONObject jsonObject = new JSONObject(content);

        String clientId = jsonObject.getString("PDF_EMBED_API_KEY");
        String clientSecret = jsonObject.getString("PDF_EMBED_API_ACCESS");

        LOGGER.debug("Credentials loaded successfully");

        return new ServicePrincipalCredentials(clientId, clientSecret);
    }

    public CredentialsBuilder addFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }
}
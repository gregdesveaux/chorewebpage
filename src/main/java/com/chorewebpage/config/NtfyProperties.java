package com.chorewebpage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ntfy")
public class NtfyProperties {

    /**
     * Base URL for the ntfy server, e.g. https://ntfy.sh or a self-hosted endpoint.
     */
    private String baseUrl = "https://ntfy.sh";

    /**
     * Optional bearer token for authenticated ntfy servers.
     */
    private String accessToken;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}

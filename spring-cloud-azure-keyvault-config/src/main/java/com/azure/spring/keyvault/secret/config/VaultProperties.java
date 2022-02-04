package com.azure.spring.keyvault.secret.config;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

public class VaultProperties implements EnvironmentAware {

    /**
     * Configuration prefix for config properties.
     */
    public static final String PREFIX = "spring.cloud.azure.keyvault.secret";

    /**
     * Enable Vault config server.
     */
    private boolean enabled = true;

    private String endpoint;



    /**
     * Fail fast if data cannot be obtained from Vault.
     */
    private boolean failFast = false;



    /**
     * Application name for AppId authentication.
     */
    private String applicationName = "application";


    @Override
    public void setEnvironment(Environment environment) {

        String springAppName = environment.getProperty("spring.application.name");

        if (StringUtils.hasText(springAppName)) {
            this.applicationName = springAppName;
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }



    public boolean isFailFast() {
        return this.failFast;
    }

    public void setFailFast(boolean failFast) {
        this.failFast = failFast;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}


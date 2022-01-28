package com.azure.spring.keyvault.secret.config;

import org.springframework.boot.context.config.ConfigDataResource;

public class KeyVaultSecretConfigLocation extends ConfigDataResource {
    /**
     * Prefix used to indicate a {@link KeyVaultSecretConfigLocation}.
     */
    public static final String VAULT_PREFIX = "keyvault-secret:";
}

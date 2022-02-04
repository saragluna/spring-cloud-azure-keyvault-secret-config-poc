package com.azure.spring.keyvault.secret.config;

import com.azure.spring.keyvault.secret.config.util.PropertyTransformers;
import org.springframework.boot.context.config.ConfigDataResource;

public class KeyVaultSecretConfigLocation extends ConfigDataResource {
    /**
     * Prefix used to indicate a {@link KeyVaultSecretConfigLocation}.
     */
    public static final String VAULT_PREFIX = "keyvault-secret:";
//    private final KeyVaultSecretBackendMetadata secretBackendMetadata;

    private final boolean optional;

    /**
     * Create a new {@link KeyVaultSecretConfigLocation} instance.
     * @param contextPath the context path
     * @param optional if the resource is optional
     */
    public KeyVaultSecretConfigLocation(String contextPath, boolean optional) {
        this(contextPath, PropertyTransformers.noop(), optional);
    }

    /**
     * Create a new {@link KeyVaultSecretConfigLocation} instance.
     * @param contextPath the context path
     * @param propertyTransformer the property transformer
     * @param optional if the resource is optional
     * @since 3.0.4
     */
    public KeyVaultSecretConfigLocation(String contextPath, PropertyTransformer propertyTransformer, boolean optional) {
        super(optional);
        this.optional = optional;
    }



    public boolean isOptional() {
        return this.optional;
    }

}

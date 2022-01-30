package com.azure.spring.keyvault.secret.config;

import java.util.Map;

public interface KeyVaultSecretBackendMetadata {
    /**
     * Return a readable name of this secret backend.
     * @return the name of this secret backend.
     */
    String getName();

    /**
     * Return the path of this secret backend.
     * @return the path of this secret backend.
     * @since 1.1
     */
    String getPath();

    /**
     * Return a {@link PropertyTransformer} to post-process properties retrieved from
     * Vault.
     * @return the property transformer.
     * @see com.azure.spring.keyvault.secret.config.util.PropertyTransformers
     */
    PropertyTransformer getPropertyTransformer();

    /**
     * @return the URL template variables. URI variables should declare either
     * {@code backend} and {@code key} or {@code path} properties.
     */
    Map<String, String> getVariables();
}

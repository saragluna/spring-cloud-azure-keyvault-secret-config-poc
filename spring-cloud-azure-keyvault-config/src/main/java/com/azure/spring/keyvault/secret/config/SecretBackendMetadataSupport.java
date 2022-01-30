package com.azure.spring.keyvault.secret.config;

import com.azure.spring.keyvault.secret.config.util.PropertyTransformers;

import java.util.Collections;
import java.util.Map;

/**
 * Support class for {@link KeyVaultSecretBackendMetadata} implementations. Implementing classes
 * are required to implement {@link #getPath()} to derive name and variables from the
 * path.
 *
 * @author Mark Paluch
 * @since 1.1
 */
public abstract class SecretBackendMetadataSupport implements KeyVaultSecretBackendMetadata {

    @Override
    public String getName() {
        return getPath();
    }

    @Override
    public PropertyTransformer getPropertyTransformer() {
        return PropertyTransformers.noop();
    }

    @Override
    public Map<String, String> getVariables() {
        return Collections.singletonMap("path", getPath());
    }

}

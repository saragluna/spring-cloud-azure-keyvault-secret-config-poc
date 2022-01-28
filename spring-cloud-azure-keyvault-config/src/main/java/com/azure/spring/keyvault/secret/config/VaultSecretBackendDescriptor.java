package com.azure.spring.keyvault.secret.config;


import org.springframework.boot.context.config.ConfigDataLocationResolver;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;

/**
 * Interface to be implemented by objects that describe a Vault secret backend. Mainly for
 * internal use within the framework.
 *
 * <p>
 * Typically, used by {@link KeyVaultSecretBackendMetadataFactory} to provide path and
 * configuration to create a {@link KeyVaultSecretBackendMetadata} object. Instances are
 * materialized through {@link Binder} and should be therefore annotated with
 * {@link org.springframework.boot.context.properties.ConfigurationProperties @ConfigurationProperties}.
 * Objects implementing this interface can be discovered {@code spring.factories} when using {@link ConfigDataLocationResolver}.
 *
 * @author Mark Paluch
 * @see KeyVaultSecretBackendMetadataFactory
 * @see KeyVaultSecretBackendMetadata
 */
public interface VaultSecretBackendDescriptor {

    /**
     * Backend path without leading/trailing slashes.
     * @return the backend path such as {@code secret} or {@code mysql}.
     */
    String getBackend();

    /**
     * @return {@literal true} if the backend is enabled.
     */
    boolean isEnabled();

}


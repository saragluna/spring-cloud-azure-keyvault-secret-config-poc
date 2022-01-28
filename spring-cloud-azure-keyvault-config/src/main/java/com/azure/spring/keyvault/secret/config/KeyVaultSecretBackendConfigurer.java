package com.azure.spring.keyvault.secret.config;
/**
 * Helps to configure {@link KeyVaultSecretBackendMetadata secret backends} with support for
 * {@link PropertyTransformer property transformers}.
 *
 * <p>
 * Assists configuration with a fluent style. This configurer allows configuration via
 * context paths and direct registration of {@link KeyVaultSecretBackendMetadata}.
 * <p>
 * Use {@link #registerDefaultKeyValueSecretBackends(boolean)} to register default kv
 * secret backend property sources and
 * {@link #registerDefaultDiscoveredSecretBackends(boolean)} to register additional secret
 * backend property sources such as MySQL and RabbitMQ.
 *
 * @author Mark Paluch
 * @since 1.1
 * @see PropertyTransformer
 * @see KeyVaultSecretBackendMetadata
 */
public interface KeyVaultSecretBackendConfigurer {
    /**
     * Add a {@link KeyVaultSecretBackendMetadata} given its {@code path}.
     * @param path must not be {@literal null} or empty.
     * @return {@code this} {@link KeyVaultSecretBackendConfigurer}.
     */
    KeyVaultSecretBackendConfigurer add(String path);

    /**
     * Add a {@link KeyVaultSecretBackendMetadata} given its {@code path} and
     * {@link PropertyTransformer}.
     * @param path must not be {@literal null} or empty.
     * @param propertyTransformer must not be {@literal null}.
     * @return {@code this} {@link KeyVaultSecretBackendConfigurer}.
     */
    KeyVaultSecretBackendConfigurer add(String path, PropertyTransformer propertyTransformer);

    /**
     * Add a {@link KeyVaultSecretBackendMetadata}.
     * @param metadata must not be {@literal null}.
     * @return {@code this} {@link KeyVaultSecretBackendConfigurer}.
     */
    KeyVaultSecretBackendConfigurer add(KeyVaultSecretBackendMetadata metadata);

    /**
     * Add a {@link KeyVaultSecretBackendMetadata} given {@link RequestedSecret}. Property sources
     * supporting leasing will derive lease renewal/rotation from
     * {@link RequestedSecret.Mode}.
     * @param requestedSecret must not be {@literal null} or empty.
     * @return {@code this} {@link KeyVaultSecretBackendConfigurer}.
     */
    KeyVaultSecretBackendConfigurer add(RequestedSecret requestedSecret);

    /**
     * Add a {@link KeyVaultSecretBackendMetadata} given {@link RequestedSecret} and
     * {@link PropertyTransformer}. Property sources supporting leasing will derive lease
     * renewal/rotation from {@link RequestedSecret.Mode}.
     * @param requestedSecret must not be {@literal null} or empty.
     * @param propertyTransformer must not be {@literal null}.
     * @return {@code this} {@link KeyVaultSecretBackendConfigurer}.
     */
    KeyVaultSecretBackendConfigurer add(RequestedSecret requestedSecret, PropertyTransformer propertyTransformer);

    /**
     * Register default key-value secret backend property sources.
     * @param registerDefault {@literal true} to enable default kv secret backend
     * registration.
     * @return {@code this} {@link KeyVaultSecretBackendConfigurer}.
     * @since 2.3.3
     */
    KeyVaultSecretBackendConfigurer registerDefaultKeyValueSecretBackends(boolean registerDefault);

    /**
     * Register default discovered secret backend property sources from
     * {@link KeyVaultSecretBackendMetadata} via {@link VaultSecretBackendDescriptor} beans.
     * @param registerDefault {@literal true} to enable default discovered secret backend
     * registration via {@link VaultSecretBackendDescriptor} beans.
     * @return {@code this} {@link KeyVaultSecretBackendConfigurer}.
     */
    KeyVaultSecretBackendConfigurer registerDefaultDiscoveredSecretBackends(boolean registerDefault);

}

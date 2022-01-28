package com.azure.spring.keyvault.secret.config;
/**
 * Defines callback methods to customize the configuration for Spring Cloud Azure KeyVault
 * applications.
 *
 * <p>
 * Configuration classes may implement this interface to be called back and given a chance
 * to customize the default configuration. Consider implementing this interface and
 * overriding the relevant methods for your needs.
 *
 * <p>
 * Registered bean instances of {@link KeyVaultSecretConfigurer} disable default secret backend
 * registration for the kv and integrative (other discovered
 * {@link KeyVaultSecretBackendMetadata}) backends. See
 * {@link KeyVaultSecretBackendConfigurer#registerDefaultDiscoveredSecretBackends(boolean)} for
 * more details.
 *
 * @author Mark Paluch
 * @since 1.1
 * @see KeyVaultSecretBackendConfigurer
 */
public interface KeyVaultSecretConfigurer {
    /**
     * Configure the secret backends that are instantiated as
     * {@link org.springframework.core.env.PropertySource property sources}.
     * @param configurer the {@link KeyVaultSecretBackendConfigurer} to configure secret backends,
     * must not be {@literal null}.
     */
    void addSecretBackends(KeyVaultSecretBackendConfigurer configurer);

}

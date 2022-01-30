package com.azure.spring.keyvault.secret.config;


import com.azure.spring.keyvault.secret.config.util.PropertyTransformers;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link KeyVaultSecretBackendMetadata} for the {@code kv} (key-value) secret backend.
 *
 * @author Mark Paluch
 * @since 2.0
 */
public class KeyValueSecretBackendMetadata extends SecretBackendMetadataSupport implements KeyVaultSecretBackendMetadata {

    private final String path;

    private final PropertyTransformer propertyTransformer;

    KeyValueSecretBackendMetadata(String path) {
        this(path, PropertyTransformers.noop());
    }

    private KeyValueSecretBackendMetadata(String path, PropertyTransformer propertyTransformer) {

        Assert.hasText(path, "Secret backend path must not be empty");
        Assert.notNull(propertyTransformer, "PropertyTransformer must not be null");

        this.path = path;
        this.propertyTransformer = propertyTransformer;
    }

    /**
     * Create a {@link KeyVaultSecretBackendMetadata} for the {@code kv} secret backend given a
     * {@code secretBackendPath} and {@code key}. Use plain mount and key paths. The
     * required {@code data} segment is added by this method.
     * @param secretBackendPath the secret backend mount path without leading/trailing
     * slashes and without the {@code data} path segment, must not be empty or
     * {@literal null}.
     * @param key the key within the secret backend. May contain slashes but not
     * leading/trailing slashes, must not be empty or {@literal null}.
     * @return the {@link KeyVaultSecretBackendMetadata}
     */
    public static KeyVaultSecretBackendMetadata create(String secretBackendPath, String key) {

        Assert.hasText(secretBackendPath, "Secret backend path must not be null or empty");
        Assert.hasText(key, "Key must not be null or empty");

        return create(String.format("%s/%s", secretBackendPath, key), UnwrappingPropertyTransformer.unwrap("data"));
    }

    /**
     * Create a {@link KeyVaultSecretBackendMetadata} for the {@code kv} secret backend given a
     * {@code path}.
     * @param path the relative path of the secret. slashes, must not be empty or
     * {@literal null}.
     * @return the {@link KeyVaultSecretBackendMetadata}
     */
    public static KeyVaultSecretBackendMetadata create(String path) {
        return new KeyValueSecretBackendMetadata(path, PropertyTransformers.noop());
    }

    /**
     * Create a {@link KeyVaultSecretBackendMetadata} for the {@code kv} secret backend given a
     * {@code path}.
     * @param path the relative path of the secret. slashes, must not be empty or
     * {@literal null}.
     * @param propertyTransformer property transformer.
     * @return the {@link KeyVaultSecretBackendMetadata}
     */
    public static KeyVaultSecretBackendMetadata create(String path, PropertyTransformer propertyTransformer) {
        return new KeyValueSecretBackendMetadata(path, propertyTransformer);
    }

    /**
     * Build a list of context paths from application name and the active profile names.
     * Application name and profiles support multiple (comma-separated) values.
     * @param properties the key-value backend properties.
     * @param profiles active application profiles.
     * @return list of context paths.
     */
    public static List<String> buildContexts(VaultKeyValueBackendPropertiesSupport properties, List<String> profiles) {

        String appName = properties.getApplicationName();

        String defaultContext = properties.getDefaultContext();
        Set<String> contexts = new LinkedHashSet<>(
            buildContexts(defaultContext, profiles, properties.getProfileSeparator()));

        for (String applicationName : StringUtils.commaDelimitedListToSet(appName)) {
            contexts.addAll(buildContexts(applicationName, profiles, properties.getProfileSeparator()));
        }

        List<String> result = new ArrayList<>(contexts);

        Collections.reverse(result);

        return result;
    }

    /**
     * Create a list of context names from a combination of application name and
     * application name with profile name. Using an empty application name will return an
     * empty list.
     * @param applicationName the application name. May be empty.
     * @param profiles active application profiles.
     * @param profileSeparator profile separator character between application name and
     * profile name.
     * @return list of context names.
     */
    public static List<String> buildContexts(String applicationName, List<String> profiles, String profileSeparator) {

        if (!StringUtils.hasText(applicationName)) {
            return Collections.emptyList();
        }

        List<String> contexts = new ArrayList<>(profiles.size() + 1);
        contexts.add(applicationName);

        for (String profile : profiles) {

            if (!StringUtils.hasText(profile)) {
                continue;
            }

            String contextName = applicationName + profileSeparator + profile.trim();

            if (!contexts.contains(contextName)) {
                contexts.add(contextName);
            }
        }

        return contexts;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public PropertyTransformer getPropertyTransformer() {
        return this.propertyTransformer;
    }

    /**
     * {@link PropertyTransformer} that strips a prefix from property names.
     */
    static final class UnwrappingPropertyTransformer implements PropertyTransformer {

        private final String prefixToStrip;

        private UnwrappingPropertyTransformer(String prefixToStrip) {

            Assert.notNull(prefixToStrip, "Property name prefix must not be null");

            this.prefixToStrip = prefixToStrip;
        }

        /**
         * Create a new {@link PropertyTransformers.KeyPrefixPropertyTransformer} that
         * adds a prefix to each key name.
         * @param propertyNamePrefix the property name prefix to be added in front of each
         * property name, must not be {@literal null}.
         * @return a new {@link PropertyTransformers.KeyPrefixPropertyTransformer} that
         * adds a prefix to each key name.
         */
        public static PropertyTransformer unwrap(String propertyNamePrefix) {
            return new UnwrappingPropertyTransformer(propertyNamePrefix);
        }

        @Override
        public Map<String, Object> transformProperties(Map<String, ? extends Object> input) {

            Map<String, Object> target = new LinkedHashMap<>(input.size(), 1);

            for (Map.Entry<String, ? extends Object> entry : input.entrySet()) {

                if (entry.getKey().startsWith(this.prefixToStrip + ".")) {
                    target.put(entry.getKey().substring(this.prefixToStrip.length() + 1), entry.getValue());
                }
                else {
                    target.put(entry.getKey(), entry.getValue());
                }
            }

            return target;
        }

    }

}


package com.azure.spring.keyvault.secret.config;

import com.azure.spring.keyvault.secret.config.util.PropertyTransformers;
import org.springframework.boot.context.config.ConfigDataResource;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class KeyVaultSecretConfigLocation extends ConfigDataResource {
    /**
     * Prefix used to indicate a {@link KeyVaultSecretConfigLocation}.
     */
    public static final String VAULT_PREFIX = "keyvault-secret:";
    private final KeyVaultSecretBackendMetadata secretBackendMetadata;

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

        Assert.hasText(contextPath, "Location must not be empty");
        validatePath(contextPath);

        this.secretBackendMetadata = KeyValueSecretBackendMetadata.create(contextPath, propertyTransformer);
        this.optional = optional;
    }

    /**
     * Create a new {@link KeyVaultSecretConfigLocation} instance.
     * @param secretBackendMetadata the backend descriptor.
     * @param optional if the resource is optional
     */
    public KeyVaultSecretConfigLocation(KeyVaultSecretBackendMetadata secretBackendMetadata, boolean optional) {

        Assert.notNull(secretBackendMetadata, "SecretBackendMetadata must not be null");

        validatePath(secretBackendMetadata.getPath());
        this.secretBackendMetadata = secretBackendMetadata;
        this.optional = optional;
    }

    public KeyVaultSecretBackendMetadata getSecretBackendMetadata() {
        return this.secretBackendMetadata;
    }

    public boolean isOptional() {
        return this.optional;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KeyVaultSecretConfigLocation)) {
            return false;
        }
        KeyVaultSecretConfigLocation that = (KeyVaultSecretConfigLocation) o;
        if (this.optional != that.optional) {
            return false;
        }
        return ObjectUtils.nullSafeEquals(this.secretBackendMetadata.getName(), that.secretBackendMetadata.getName())
            && ObjectUtils.nullSafeEquals(this.secretBackendMetadata.getPath(),
            that.secretBackendMetadata.getPath());
    }

    @Override
    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(this.secretBackendMetadata.getName());
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.secretBackendMetadata.getPath());
        result = 31 * result + (this.optional ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getSimpleName());
        sb.append(" [path='").append(this.secretBackendMetadata.getPath()).append('\'');
        sb.append(", optional=").append(this.optional);
        sb.append(']');
        return sb.toString();
    }

    private static void validatePath(String contextPath) {
        Assert.isTrue(!contextPath.endsWith("/"),
            () -> String.format("Location 'vault://%s' must not end with a trailing slash", contextPath));
    }

}

package com.azure.spring.keyvault.secret.config;

import com.azure.security.keyvault.secrets.SecretAsyncClient;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.azure.security.keyvault.secrets.models.SecretProperties;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.function.Predicate;

public class VaultConfigTemplate implements VaultConfigOperations {
    private final SecretAsyncClient client;

    public VaultConfigTemplate(SecretAsyncClient client) {
        this.client = client;
    }

    @Override
    public Flux<KeyVaultSecret> readAll() {
       return client.listPropertiesOfSecrets()
              .filter(isEnabled.and(isEffective).and(isNotExpired))
              .flatMap(sp -> {
                  return client.getSecret(sp.getName(), sp.getVersion());
              });
    }

    private static final Predicate<SecretProperties> isEffective = (sp) -> {
        return sp.getNotBefore() == null || OffsetDateTime.now().isAfter(sp.getNotBefore());
    };

    private static final Predicate<SecretProperties> isNotExpired = (sp) -> {
        return sp.getExpiresOn() == null || OffsetDateTime.now().isBefore(sp.getExpiresOn());
    };

    private static final Predicate<SecretProperties> isEnabled = SecretProperties::isEnabled;
}

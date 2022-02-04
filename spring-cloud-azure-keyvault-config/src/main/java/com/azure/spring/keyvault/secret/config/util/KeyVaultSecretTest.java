package com.azure.spring.keyvault.secret.config.util;

import com.azure.security.keyvault.secrets.SecretAsyncClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.azure.security.keyvault.secrets.models.SecretProperties;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.function.Predicate;

public class KeyVaultSecretTest {
    public static void main(String[] args) {
        SecretClientBuilder builder = new SecretClientBuilder();

        SecretAsyncClient client = builder.buildAsyncClient();


        Flux<KeyVaultSecret> secretFlux =
            client.listPropertiesOfSecrets()
                  .filter(isEnabled.and(isEffective).and(isNotExpired))
                  .flatMap(sp -> {
                      return client.getSecret(sp.getName(), sp.getVersion());
                  });

        client.getSecret("secret-name").subscribe(secret -> {
            secret.getName();
            secret.getValue();
            secret.getId();
            SecretProperties properties = secret.getProperties();
            System.out.println(secret.getValue());
        });
    }

    private static Predicate<SecretProperties> isEffective = (sp) -> {
        return sp.getNotBefore() == null || OffsetDateTime.now().isAfter(sp.getNotBefore());
    };

    private static Predicate<SecretProperties> isNotExpired = (sp) -> {
        return sp.getExpiresOn() == null || OffsetDateTime.now().isBefore(sp.getExpiresOn());
    };

    private static Predicate<SecretProperties> isEnabled = SecretProperties::isEnabled;
}

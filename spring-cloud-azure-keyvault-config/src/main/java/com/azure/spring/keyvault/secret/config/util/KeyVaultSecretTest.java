package com.azure.spring.keyvault.secret.config.util;

import com.azure.security.keyvault.secrets.SecretAsyncClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.SecretProperties;

public class KeyVaultSecretTest {
    public static void main(String[] args) {
        SecretClientBuilder builder = new SecretClientBuilder();

       SecretAsyncClient client= builder.buildAsyncClient();

       client.getSecret("secret-name").subscribe(secret -> {
          secret.getName();
          secret.getValue();
          secret.getId();
         SecretProperties properties = secret.getProperties();
           System.out.println(secret.getValue());
       });
    }
}

package com.azure.spring.keyvault.secret.config;

import org.springframework.boot.context.config.ConfigDataLocation;
import org.springframework.boot.context.config.ConfigDataLocationNotFoundException;
import org.springframework.boot.context.config.ConfigDataLocationResolver;
import org.springframework.boot.context.config.ConfigDataLocationResolverContext;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.boot.context.config.Profiles;

import java.util.Collections;
import java.util.List;

public class KeyVaultSecretConfigDataLocationResolver implements ConfigDataLocationResolver<KeyVaultSecretConfigLocation> {

    @Override
    public boolean isResolvable(ConfigDataLocationResolverContext context, ConfigDataLocation location) {
        return location.getValue().startsWith(KeyVaultSecretConfigLocation.VAULT_PREFIX);
    }

    @Override
    public List<KeyVaultSecretConfigLocation> resolve(ConfigDataLocationResolverContext context, ConfigDataLocation location)
        throws ConfigDataLocationNotFoundException, ConfigDataResourceNotFoundException {
        return Collections.emptyList();
    }

    @Override
    public List<KeyVaultSecretConfigLocation> resolveProfileSpecific(ConfigDataLocationResolverContext context,
                                                                     ConfigDataLocation location, Profiles profiles) throws ConfigDataLocationNotFoundException {
        if (!location.getValue().startsWith(KeyVaultSecretConfigLocation.VAULT_PREFIX)) {
            return Collections.emptyList();
        }

        registerVaultProperties(context);

        if (location.getValue().equals(KeyVaultSecretConfigLocation.VAULT_PREFIX)
            || location.getValue().equals(KeyVaultSecretConfigLocation.VAULT_PREFIX + "//")) {
            return Collections.singletonList(new KeyVaultSecretConfigLocation(location.getValue(), location.isOptional()));
        }
        return Collections.emptyList();
    }



    private static void registerVaultProperties(ConfigDataLocationResolverContext context) {

        context.getBootstrapContext().registerIfAbsent(VaultProperties.class, ignore -> {

            VaultProperties vaultProperties = context.getBinder().bindOrCreate(VaultProperties.PREFIX,
                VaultProperties.class);

            vaultProperties.setApplicationName(context.getBinder().bind("spring.application.name", String.class)
                                                      .orElse(vaultProperties.getApplicationName()));

            return vaultProperties;
        });
    }
}

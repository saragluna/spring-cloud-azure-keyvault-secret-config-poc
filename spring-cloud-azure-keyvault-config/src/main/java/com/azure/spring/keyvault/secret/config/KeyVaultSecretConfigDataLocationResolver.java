package com.azure.spring.keyvault.secret.config;

import org.springframework.boot.context.config.ConfigDataLocation;
import org.springframework.boot.context.config.ConfigDataLocationNotFoundException;
import org.springframework.boot.context.config.ConfigDataLocationResolver;
import org.springframework.boot.context.config.ConfigDataLocationResolverContext;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.boot.context.config.Profiles;

import java.util.List;

public class KeyVaultSecretConfigDataLocationResolver implements ConfigDataLocationResolver<KeyVaultSecretConfigLocation> {
    @Override
    public boolean isResolvable(ConfigDataLocationResolverContext context, ConfigDataLocation location) {
        return false;
    }

    @Override
    public List<KeyVaultSecretConfigLocation> resolve(ConfigDataLocationResolverContext context,
                                                      ConfigDataLocation location) throws ConfigDataLocationNotFoundException, ConfigDataResourceNotFoundException {
        return null;
    }

    @Override
    public List<KeyVaultSecretConfigLocation> resolveProfileSpecific(ConfigDataLocationResolverContext context,
                                                                     ConfigDataLocation location, Profiles profiles) throws ConfigDataLocationNotFoundException {
        return ConfigDataLocationResolver.super.resolveProfileSpecific(context, location, profiles);
    }
}

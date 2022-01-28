package com.azure.spring.keyvault.secret.config;

import org.springframework.boot.context.config.ConfigData;
import org.springframework.boot.context.config.ConfigDataLoader;
import org.springframework.boot.context.config.ConfigDataLoaderContext;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;

import java.io.IOException;

public class KeyVaultSecretConfigDataLoader implements ConfigDataLoader<KeyVaultSecretConfigLocation> {
    @Override
    public boolean isLoadable(ConfigDataLoaderContext context, KeyVaultSecretConfigLocation resource) {
        return ConfigDataLoader.super.isLoadable(context, resource);
    }

    @Override
    public ConfigData load(ConfigDataLoaderContext context, KeyVaultSecretConfigLocation resource) throws IOException, ConfigDataResourceNotFoundException {
        return null;
    }
}

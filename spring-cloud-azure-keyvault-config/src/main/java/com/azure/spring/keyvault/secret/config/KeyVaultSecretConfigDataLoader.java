package com.azure.spring.keyvault.secret.config;

import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretAsyncClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import org.apache.commons.logging.Log;
import org.springframework.boot.BootstrapContext;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.context.config.ConfigData;
import org.springframework.boot.context.config.ConfigDataLoader;
import org.springframework.boot.context.config.ConfigDataLoaderContext;
import org.springframework.boot.context.config.ConfigDataLocationNotFoundException;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.PropertySource;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class KeyVaultSecretConfigDataLoader implements ConfigDataLoader<KeyVaultSecretConfigLocation> {
    private final static ConfigData SKIP_LOCATION = null;


    private final DeferredLogFactory logFactory;

    public KeyVaultSecretConfigDataLoader(DeferredLogFactory logFactory) {
        this.logFactory = logFactory;
    }

    @Override
    public ConfigData load(ConfigDataLoaderContext context, KeyVaultSecretConfigLocation location)
        throws ConfigDataLocationNotFoundException {

        ConfigurableBootstrapContext bootstrap = context.getBootstrapContext();
        VaultProperties vaultProperties = bootstrap.get(VaultProperties.class);

        if (!vaultProperties.isEnabled()) {
            return SKIP_LOCATION;
        }
        return loadConfigData(location, bootstrap, vaultProperties);
    }

    private ConfigData loadConfigData(KeyVaultSecretConfigLocation location, ConfigurableBootstrapContext bootstrap,
                                      VaultProperties vaultProperties) {
        registerIfAbsent(bootstrap, "defaultAzureCredential", DefaultAzureCredential.class, () -> {
            return new DefaultAzureCredentialBuilder().build();
        });
        registerIfAbsent(bootstrap, "secretAsyncClient", SecretAsyncClient.class, () -> {
            DefaultAzureCredential credential = bootstrap.get(DefaultAzureCredential.class);

            SecretClientBuilder builder = new SecretClientBuilder();
            return builder.credential(credential)
                          .vaultUrl(vaultProperties.getEndpoint())
                          .buildAsyncClient();

        });
        registerVaultConfigTemplate(bootstrap, vaultProperties);

        return createConfigData(() -> {
            VaultConfigTemplate configTemplate = bootstrap.get(VaultConfigTemplate.class);

            return createVaultPropertySource(configTemplate, vaultProperties.isFailFast());
        });
    }




    static ConfigData createConfigData(Supplier<PropertySource<?>> propertySourceSupplier) {
        return new ConfigData(Collections.singleton(propertySourceSupplier.get()));
    }

    private void registerVaultConfigTemplate(ConfigurableBootstrapContext bootstrap, VaultProperties vaultProperties) {
        bootstrap.registerIfAbsent(VaultConfigTemplate.class,
            ctx -> new VaultConfigTemplate(ctx.get(SecretAsyncClient.class)));
    }



    private PropertySource<?> createVaultPropertySource(VaultConfigOperations configOperations, boolean failFast) {

        //TODO we need to give a proper name for the key vault
        KeyVaultSecretPropertySource vaultPropertySource = new KeyVaultSecretPropertySource("default", configOperations,
            failFast);
        vaultPropertySource.init();
        return vaultPropertySource;
    }



    static <T> void registerIfAbsent(ConfigurableBootstrapContext bootstrap, String beanName, Class<T> instanceType,
                                     Supplier<T> instanceSupplier) {
        registerIfAbsent(bootstrap, beanName, instanceType, ctx -> instanceSupplier.get(), ctx -> {
        });
    }

    static <T> void registerIfAbsent(ConfigurableBootstrapContext bootstrap, String beanName, Class<T> instanceType,
                                     Function<BootstrapContext, T> instanceSupplier,
                                     Consumer<ConfigurableApplicationContext> contextCustomizer) {

        bootstrap.registerIfAbsent(instanceType, instanceSupplier::apply);

        bootstrap.addCloseListener(event -> {

            GenericApplicationContext gac = (GenericApplicationContext) event.getApplicationContext();

            contextCustomizer.accept(gac);
            T instance = event.getBootstrapContext().get(instanceType);

            gac.registerBean(beanName, instanceType, () -> instance);
        });
    }



}

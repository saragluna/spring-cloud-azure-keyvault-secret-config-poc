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
//            List<KeyVaultSecretBackendMetadata> sorted = getSecretBackends(context, profiles);
//            return sorted.stream().map(it -> new KeyVaultSecretConfigLocation(it, location.isOptional()))
//                         .collect(Collectors.toList());

            return Collections.singletonList(new KeyVaultSecretConfigLocation(location.getValue(), location.isOptional()));
        }
        return Collections.emptyList();

//        String contextPath = location.getValue().substring(KeyVaultSecretConfigLocation.VAULT_PREFIX.length());

//        while (contextPath.startsWith("/")) {
//            contextPath = contextPath.substring(1);
//        }
//
//        return Collections.singletonList(
//            new KeyVaultSecretConfigLocation(contextPath, getPropertyTransformer(contextPath), location.isOptional()));
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

//    /**
//     * supports prefix in contextual path {@link https://docs.spring.io/spring-cloud-vault/docs/current/reference/html/#vault.configdata.locations}
//     */
//    private static PropertyTransformer getPropertyTransformer(String contextPath) {
//
//        UriComponents uriComponents = UriComponentsBuilder.fromUriString(contextPath).build();
//        String prefix = uriComponents.getQueryParams().getFirst("prefix");
//
//        if (StringUtils.hasText(prefix) && StringUtils.hasText(uriComponents.getPath())) {
//            return PropertyTransformers.propertyNamePrefix(prefix);
//        }
//
//        return PropertyTransformers.noop();
//    }
}

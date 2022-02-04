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
        //        reconfigureLoggers(logFactory);
    }

    @Override
    public ConfigData load(ConfigDataLoaderContext context, KeyVaultSecretConfigLocation location)
        throws ConfigDataLocationNotFoundException {

        ConfigurableBootstrapContext bootstrap = context.getBootstrapContext();
        VaultProperties vaultProperties = bootstrap.get(VaultProperties.class);

        if (!vaultProperties.isEnabled()) {
            return SKIP_LOCATION;
        }

        //        if (vaultProperties.getSession().getLifecycle().isEnabled()
        //            || vaultProperties.getConfig().getLifecycle().isEnabled()) {
        //            registerVaultTaskScheduler(bootstrap);
        //        }


        //        registerReactiveInfrastructure(bootstrap, vaultProperties);


        //        registerVaultConfigTemplate(bootstrap, vaultProperties);

        //        if (vaultProperties.getConfig().getLifecycle().isEnabled()) {
        //            registerSecretLeaseContainer(bootstrap, new VaultConfiguration(vaultProperties));
        //        }

        return loadConfigData(location, bootstrap, vaultProperties);
    }

    private ConfigData loadConfigData(KeyVaultSecretConfigLocation location, ConfigurableBootstrapContext bootstrap,
                                      VaultProperties vaultProperties) {

        //        if (location.getSecretBackendMetadata() instanceof ApplicationEventPublisherAware) {
        //
        //            bootstrap.addCloseListener(event -> {
        //                ((ApplicationEventPublisherAware) location.getSecretBackendMetadata())
        //                    .setApplicationEventPublisher(event.getApplicationContext());
        //            });
        //        }

        //        if (vaultProperties.getConfig().getLifecycle().isEnabled()) {
        //
        //            RequestedSecret secret = getRequestedSecret(location.getSecretBackendMetadata());
        //
        //            if (vaultProperties.isFailFast()) {
        //                return new ConfigData(Collections.singleton(createLeasingPropertySourceFailFast(
        //                    bootstrap.get(SecretLeaseContainer.class), secret, location.getSecretBackendMetadata())));
        //            }
        //
        //            return createConfigData(() -> createLeasingPropertySource(bootstrap.get(SecretLeaseContainer
        //            .class), secret,
        //                location.getSecretBackendMetadata()));
        //        }

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


    //    private void registerReactiveInfrastructure(ConfigurableBootstrapContext bootstrap,
    //                                                VaultProperties vaultProperties) {
    //
    //        ReactiveInfrastructure reactiveInfrastructure = new ReactiveInfrastructure(bootstrap, vaultProperties,
    //            this.logFactory);
    //        reactiveInfrastructure.registerClientHttpConnectorWrapper();
    //        reactiveInfrastructure.registerWebClientBuilder();
    //        reactiveInfrastructure.registerWebClientFactory();
    //
    //        VaultProperties.AuthenticationMethod authentication = vaultProperties.getAuthentication();
    //
    //        if (authentication == VaultProperties.AuthenticationMethod.NONE) {
    //            registerIfAbsent(bootstrap, "reactiveVaultTemplate", ReactiveVaultTemplate.class,
    //                ctx -> new ReactiveVaultTemplate(ctx.get(WebClientBuilder.class)));
    //        } else {
    //
    //            reactiveInfrastructure.registerTokenSupplier();
    //            reactiveInfrastructure.registerReactiveSessionManager();
    //            reactiveInfrastructure.registerSessionManager();
    //
    //            registerIfAbsent(bootstrap, "reactiveVaultTemplate", ReactiveVaultTemplate.class,
    //                ctx -> new ReactiveVaultTemplate(bootstrap.get(WebClientBuilder.class),
    //                    bootstrap.get(ReactiveSessionManager.class)));
    //        }
    //    }

    static ConfigData createConfigData(Supplier<PropertySource<?>> propertySourceSupplier) {
        return new ConfigData(Collections.singleton(propertySourceSupplier.get()));
    }

    private void registerVaultConfigTemplate(ConfigurableBootstrapContext bootstrap, VaultProperties vaultProperties) {
        bootstrap.registerIfAbsent(VaultConfigTemplate.class,
            ctx -> new VaultConfigTemplate(ctx.get(SecretAsyncClient.class)));
    }

    //    private void registerVaultTaskScheduler(ConfigurableBootstrapContext bootstrap) {
    //        registerIfAbsent(bootstrap, "vaultTaskScheduler", TaskSchedulerWrapper.class, () -> {
    //
    //            ThreadPoolTaskScheduler scheduler = VaultConfiguration.createScheduler();
    //
    //            scheduler.afterPropertiesSet();
    //
    //            // avoid double-initialization
    //            return new TaskSchedulerWrapper(scheduler, false);
    //        }, ConfigurableApplicationContext::registerShutdownHook);
    //    }

    //    private void registerSecretLeaseContainer(ConfigurableBootstrapContext bootstrap,
    //                                              VaultConfiguration vaultConfiguration) {
    //        registerIfAbsent(bootstrap, "secretLeaseContainer", SecretLeaseContainer.class, ctx -> {
    //
    //            SecretLeaseContainer container = vaultConfiguration.createSecretLeaseContainer(ctx.get
    //            (VaultTemplate.class),
    //                () -> ctx.get(TaskSchedulerWrapper.class).getTaskScheduler());
    //
    //            try {
    //                container.afterPropertiesSet();
    //            } catch (Exception e) {
    //                ReflectionUtils.rethrowRuntimeException(e);
    //            }
    //            container.start();
    //
    //            return container;
    //        }, ConfigurableApplicationContext::registerShutdownHook);
    //    }

    private PropertySource<?> createVaultPropertySource(VaultConfigOperations configOperations, boolean failFast) {

        //TODO we need to give a proper name for the key vault
        KeyVaultSecretPropertySource vaultPropertySource = new KeyVaultSecretPropertySource("default", configOperations,
            failFast);
        vaultPropertySource.init();
        return vaultPropertySource;
    }

    //    private PropertySource<?> createLeasingPropertySource(SecretLeaseContainer secretLeaseContainer,
    //                                                          RequestedSecret secret, KeyVaultSecretBackendMetadata
    //                                                          accessor) {
    //
    //        if (accessor instanceof LeasingSecretBackendMetadata) {
    //            ((LeasingSecretBackendMetadata) accessor).beforeRegistration(secret, secretLeaseContainer);
    //        }
    //
    //        LeaseAwareVaultPropertySource propertySource = new LeaseAwareVaultPropertySource(accessor.getName(),
    //            secretLeaseContainer, secret, accessor.getPropertyTransformer());
    //
    //        if (accessor instanceof LeasingSecretBackendMetadata) {
    //            ((LeasingSecretBackendMetadata) accessor).afterRegistration(secret, secretLeaseContainer);
    //        }
    //
    //        return propertySource;
    //    }

    //    private PropertySource<?> createLeasingPropertySourceFailFast(SecretLeaseContainer secretLeaseContainer,
    //                                                                  RequestedSecret secret,
    //                                                                  KeyVaultSecretBackendMetadata accessor) {
    //
    //        final AtomicReference<Exception> errorRef = new AtomicReference<>();
    //
    //        LeaseErrorListener errorListener = (leaseEvent, exception) -> {
    //
    //            if (leaseEvent.getSource() == secret) {
    //                errorRef.compareAndSet(null, exception);
    //            }
    //        };
    //
    //        secretLeaseContainer.addErrorListener(errorListener);
    //        try {
    //            return createLeasingPropertySource(secretLeaseContainer, secret, accessor);
    //        } finally {
    //            secretLeaseContainer.removeLeaseErrorListener(errorListener);
    //
    //            Exception exception = errorRef.get();
    //            if (exception != null) {
    //                if (exception instanceof VaultException) {
    //                    throw (VaultException) exception;
    //                }
    //                throw new VaultException(
    //                    String.format("Cannot initialize PropertySource for secret at %s", secret.getPath()),
    //                    exception);
    //            }
    //        }
    //    }

    //    private RequestedSecret getRequestedSecret(KeyVaultSecretBackendMetadata accessor) {
    //
    //        //        if (accessor instanceof LeasingSecretBackendMetadata) {
    //        //
    //        //            LeasingSecretBackendMetadata leasingBackend = (LeasingSecretBackendMetadata) accessor;
    //        //            return RequestedSecret.from(leasingBackend.getLeaseMode(), accessor.getPath());
    //        //        }
    //
    //        if (accessor instanceof KeyValueSecretBackendMetadata) {
    //            return RequestedSecret.rotating(accessor.getPath());
    //        }
    //
    //        return RequestedSecret.renewable(accessor.getPath());
    //    }

    //    static <T> void registerIfAbsent(ConfigurableBootstrapContext bootstrap, String beanName, Class<T>
    //    instanceType,
    //                                     Supplier<T> instanceSupplier) {
    //        registerIfAbsent(bootstrap, beanName, instanceType, ctx -> instanceSupplier.get(), ctx -> {
    //        });
    //    }
    //
    //    static <T> void registerIfAbsent(ConfigurableBootstrapContext bootstrap, String beanName, Class<T>
    //    instanceType,
    //                                     Supplier<T> instanceSupplier,
    //                                     Consumer<ConfigurableApplicationContext> contextCustomizer) {
    //        registerIfAbsent(bootstrap, beanName, instanceType, ctx -> instanceSupplier.get(), contextCustomizer);
    //    }
    //
    //    static <T> void registerIfAbsent(ConfigurableBootstrapContext bootstrap, String beanName, Class<T>
    //    instanceType,
    //                                     Function<BootstrapContext, T> instanceSupplier) {
    //        registerIfAbsent(bootstrap, beanName, instanceType, instanceSupplier, ctx -> {
    //        });
    //    }

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

    //    static void reconfigureLoggers(DeferredLogFactory logFactory) {
    //
    //        List<Class<?>> loggers = Arrays.asList(ClientHttpRequestFactoryFactory.class,
    //            KeyVaultSecretPropertySource.class,
    //            LeaseAwareVaultPropertySource.class,
    //            forName("org.springframework.vault.core.lease.SecretLeaseContainer$LeaseRenewalScheduler"),
    //            forName("org.springframework.vault.core.lease.SecretLeaseEventPublisher$LoggingErrorListener"));
    //
    //        loggers.forEach(it -> reconfigureLogger(it, logFactory));
    //    }
    //
    //    static void reconfigureLogger(Class<?> type, DeferredLogFactory logFactory) {
    //
    //        ReflectionUtils.doWithFields(type, field -> {
    //
    //            field.setAccessible(true);
    //            field.set(null, logFactory.getLog(type));
    //
    //        }, KeyVaultSecretConfigDataLoader::isUpdateableLogField);
    //    }
    //
    //    static void reconfigureLogger(Object object, DeferredLogFactory logFactory) {
    //
    //        ReflectionUtils.doWithFields(object.getClass(), field -> {
    //
    //            field.setAccessible(true);
    //            field.set(object, logFactory.getLog(object.getClass()));
    //
    //        }, KeyVaultSecretConfigDataLoader::isUpdateableLogField);
    //    }

    //    static boolean isUpdateableLogField(Field field) {
    //        return !Modifier.isFinal(field.getModifiers()) && field.getType().isAssignableFrom(Log.class);
    //    }
    //
    //    @Nullable
    //    static Class<?> forName(String name) {
    //        try {
    //            return ClassUtils.forName(name, KeyVaultSecretConfigDataLocationResolver.class.getClassLoader());
    //        } catch (ClassNotFoundException e) {
    //            return null;
    //        }
    //    }


    //    /**
    //     * Support class to register reactive infrastructure bootstrap instances and beans. Mirrors {@link
    //     * VaultReactiveAutoConfiguration}.
    //     */
    //    static class ReactiveInfrastructure {
    //
    //        private final ConfigurableBootstrapContext bootstrap;
    //
    //        private final VaultReactiveConfiguration configuration;
    //
    //        private final VaultEndpointProvider endpointProvider;
    //
    //        private final DeferredLogFactory logFactory;
    //
    //        ReactiveInfrastructure(ConfigurableBootstrapContext bootstrap, VaultProperties vaultProperties,
    //                               DeferredLogFactory logFactory) {
    //            this.bootstrap = bootstrap;
    //            this.configuration = new VaultReactiveConfiguration(vaultProperties);
    //            this.endpointProvider = SimpleVaultEndpointProvider
    //                .of(new VaultConfiguration(vaultProperties).createVaultEndpoint());
    //            this.logFactory = logFactory;
    //        }
    //
    //        void registerClientHttpConnectorWrapper() {
    //            registerIfAbsent(this.bootstrap, "clientHttpConnectorWrapper", ClientHttpConnectorWrapper.class,
    //                () -> new ClientHttpConnectorWrapper(this.configuration.createClientHttpConnector()));
    //        }
    //
    //        public void registerWebClientBuilder() {
    //            // not a bean
    //            this.bootstrap.registerIfAbsent(WebClientBuilder.class,
    //                ctx -> this.configuration.createWebClientBuilder(
    //                    ctx.get(ClientHttpConnectorWrapper.class).getConnector(), this.endpointProvider,
    //                    Collections.emptyList()));
    //        }
    //
    //        void registerWebClientFactory() {
    //            registerIfAbsent(this.bootstrap, "vaultWebClientFactory", WebClientFactory.class,
    //                ctx -> new DefaultWebClientFactory(ctx.get(ClientHttpConnectorWrapper.class).getConnector(),
    //                    connector -> this.configuration.createWebClientBuilder(connector, this.endpointProvider,
    //                        Collections.emptyList())));
    //        }
    //
    //        void registerTokenSupplier() {
    //
    //            registerIfAbsent(this.bootstrap, "vaultTokenSupplier", VaultTokenSupplier.class,
    //                ctx -> this.configuration.createVaultTokenSupplier(ctx.get(WebClientFactory.class), () -> {
    //                    if (this.bootstrap.isRegistered(AuthenticationStepsFactory.class)) {
    //                        return this.bootstrap.get(AuthenticationStepsFactory.class);
    //                    }
    //
    //                    return null;
    //                }, () -> {
    //                    if (this.bootstrap.isRegistered(ClientAuthentication.class)) {
    //                        return this.bootstrap.get(ClientAuthentication.class);
    //                    }
    //
    //                    return null;
    //                }));
    //        }
    //
    ////        void registerReactiveSessionManager() {
    ////
    ////            registerIfAbsent(this.bootstrap, "reactiveVaultSessionManager", ReactiveSessionManager.class,
    ////                ctx -> this.configuration.createReactiveSessionManager(ctx.get(VaultTokenSupplier.class),
    ////                    () -> ctx.get(TaskSchedulerWrapper.class).getTaskScheduler(),
    ////                    ctx.get(WebClientFactory.class)));
    ////        }
    ////
    ////        void registerSessionManager() {
    ////            registerIfAbsent(this.bootstrap, "vaultSessionManager", SessionManager.class, ctx -> {
    ////                SessionManager sessionManager = this.configuration
    ////                    .createSessionManager(ctx.get(ReactiveSessionManager.class));
    ////                reconfigureLogger(sessionManager, this.logFactory);
    ////                return sessionManager;
    ////            });
    ////        }
    //
    //    }

    //    /**
    //     * Wrapper for {@link ClientHttpRequestFactory} that suppresses {@link #afterPropertiesSet()} to avoid
    //     * double-initialization.
    //     */
    //    private static class NonInitializingClientFactoryWrapper extends ClientFactoryWrapper {
    //
    //        NonInitializingClientFactoryWrapper(ClientHttpRequestFactory clientHttpRequestFactory) {
    //            super(clientHttpRequestFactory);
    //        }
    //
    //        @Override
    //        public void afterPropertiesSet() {
    //        }
    //
    //    }

}

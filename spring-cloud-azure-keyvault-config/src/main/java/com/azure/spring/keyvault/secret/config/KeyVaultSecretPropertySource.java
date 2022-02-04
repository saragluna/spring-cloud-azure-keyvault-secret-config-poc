/*
 * Copyright 2016-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.azure.spring.keyvault.secret.config;

import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A {@link EnumerablePropertySource} backed by {@link VaultConfigTemplate}.
 *
 * @author Spencer Gibb
 * @author Mark Paluch
 */
class KeyVaultSecretPropertySource extends EnumerablePropertySource<VaultConfigOperations> {

    private static final Log log = LogFactory.getLog(KeyVaultSecretPropertySource.class);

    private final boolean failFast;

    private final String keyvaultName;
    private final Map<String, Object> properties = new LinkedHashMap<>();

    @Nullable
    private Flux<KeyVaultSecret> secrets;

    /**
     * Creates a new {@link KeyVaultSecretPropertySource}.
     *
     * @param operations must not be {@literal null}.
     * @param failFast fail if properties could not be read because of access errors.
     */
    KeyVaultSecretPropertySource(String name, VaultConfigOperations operations, boolean failFast) {

        super(name, operations);

        Assert.notNull(operations, "VaultConfigTemplate must not be null!");
        Assert.notNull(name, "key vault name must not be null!");
        this.keyvaultName = name;
        this.failFast = failFast;
    }

    /**
     * Initialize property source and read properties from Vault.
     */
    public void init() {

        try {
            this.secrets = this.source.readAll();
            this.secrets.subscribe(secret -> {
                log.info("================ "+secret);
                this.properties.put(secret.getName(), secret.getValue());
            });

        } catch (RuntimeException e) {

            String message = String.format("Unable to read properties from Azure Key Vault Secret %s ",
                getName());

            if (this.failFast) {
                throw e;
            }

            log.error(message, e);
        }
    }

    @Override
    public Object getProperty(String name) {
        return this.properties.get(name);
    }

    @Override
    public String[] getPropertyNames() {
        Set<String> strings = this.properties.keySet();
        return strings.toArray(new String[0]);
    }

}

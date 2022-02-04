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
import reactor.core.publisher.Flux;


/**
 * Interface that specified a basic set of Vault operations, implemented by
 * {@link VaultConfigTemplate}.
 *
 * @author Mark Paluch
 * @see VaultConfigTemplate
 */
public interface VaultConfigOperations {

	/**
	 * Read secrets from a secret backend encapsulated within a
	 * secret backends that do not require a request body.
	 * @return the configuration data. May be {@literal null}.
	 * @throws IllegalStateException if {@link VaultProperties#failFast} is enabled.
	 */
    Flux<KeyVaultSecret> readAll();

}

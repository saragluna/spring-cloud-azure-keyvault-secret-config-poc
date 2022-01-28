This is a PoC project used for testing the Spring Boot Config API with Azure KeyVault Secret.

We should provide simliar features like [Spring Cloud Vault](https://docs.spring.io/spring-cloud-vault/docs/current/reference/html/#vault.configdata)

According to Spring cloud vault, we should support two config locations:

```
vault:// (default location)
vault:///<context-path> (contextual location)
```
Without further configuration, Spring Cloud Vault mounts the key-value backend at `/secret/${spring.application.name}`. Each activated profile adds another context path following the form `/secret/${spring.application.name}/${profile}`.

Or the contextual location can be used to control which context paths are mounted from Vault as PropertySource, you can either use a contextual location (`vault:///my/context/path`) or configure a VaultConfigurer.

Contextual locations are specified and mounted individually. Spring Cloud Vault mounts each location as a unique **PropertySource**. You can mix the default locations with contextual locations (or other config systems) to control the order of property sources. This approach is useful in particular if you want to disable the default key-value path computation and mount each key-value backend yourself instead.

```yaml
spring.config.import: vault://first/context/path, vault://other/path, vault://
```

### property prefix

Property names within a Spring Environment must be unique to avoid shadowing. If you use the same secret names in different context paths and you want to expose these as individual properties you can distinguish them by adding a prefix query parameter to the location.


```yaml
spring.config.import: vault://my/path?prefix=foo., vault://my/other/path?prefix=bar.
secret: ${foo.secret}
other.secret: ${bar.secret}
```

### Conditionally enable/disable Vault Configuration

In some cases, it can be required to launch an application without Vault. You can express whether a Vault config location should be optional or mandatory (default) through the location string:
```
optional:vault:// (default location)
optional:vault:///<context-path> (contextual location)
```


Optional locations are skipped during application startup if Vault support was disabled through     `spring.cloud.vault.enabled=false`.


### Avoid create multiple Vault Clients

https://docs.spring.io/spring-cloud-vault/docs/current/reference/html/#vault.configdata.customization
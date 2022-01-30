package com.azure.spring.keyvault.secret.config.util;


import com.azure.spring.keyvault.secret.config.PropertyTransformer;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementations of {@link PropertyTransformer} that provide various useful property
 * transformation operations, prefixing, etc.
 *
 * @author Mark Paluch
 */
public abstract class PropertyTransformers {

    /**
     * @return "no-operation" transformer which simply returns given name as is. Used
     * commonly as placeholder or marker.
     */
    public static PropertyTransformer noop() {
        return NoOpPropertyTransformer.instance();
    }

    /**
     * @return removes {@literal null} value properties.
     */
    public static PropertyTransformer removeNullProperties() {
        return RemoveNullProperties.instance();
    }

    /**
     * @param propertyNamePrefix the prefix to add to each property name.
     * @return {@link PropertyTransformer} to add {@code propertyNamePrefix} to each
     * property name.
     */
    public static PropertyTransformer propertyNamePrefix(String propertyNamePrefix) {
        return KeyPrefixPropertyTransformer.forPrefix(propertyNamePrefix);
    }

    /**
     * {@link PropertyTransformer} that passes the given properties through without
     * returning changed properties.
     */
    static class NoOpPropertyTransformer implements PropertyTransformer {

        static NoOpPropertyTransformer INSTANCE = new NoOpPropertyTransformer();

        private NoOpPropertyTransformer() {
        }

        /**
         * @return the {@link PropertyTransformer} instance.
         */
        public static PropertyTransformer instance() {
            return INSTANCE;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Map<String, Object> transformProperties(Map<String, ? extends Object> input) {
            return (Map) input;
        }

    }

    /**
     * {@link PropertyTransformer} to remove {@literal null}-value properties.
     */
    static class RemoveNullProperties implements PropertyTransformer {

        static RemoveNullProperties INSTANCE = new RemoveNullProperties();

        private RemoveNullProperties() {
        }

        /**
         * @return the {@link PropertyTransformer} instance.
         */
        public static PropertyTransformer instance() {
            return INSTANCE;
        }

        @Override
        public Map<String, Object> transformProperties(Map<String, ? extends Object> input) {

            Map<String, Object> target = new LinkedHashMap<>(input.size(), 1);

            for (Map.Entry<String, ? extends Object> entry : input.entrySet()) {

                if (entry.getValue() == null) {
                    continue;
                }

                target.put(entry.getKey(), entry.getValue());
            }

            return target;
        }

    }

    /**
     * {@link PropertyTransformer} that adds a prefix to each key name.
     */
    static class KeyPrefixPropertyTransformer implements PropertyTransformer {

        private final String propertyNamePrefix;

        private KeyPrefixPropertyTransformer(String propertyNamePrefix) {

            Assert.notNull(propertyNamePrefix, "Property name prefix must not be null");

            this.propertyNamePrefix = propertyNamePrefix;
        }

        /**
         * Create a new {@link KeyPrefixPropertyTransformer} that adds a prefix to each
         * key name.
         * @param propertyNamePrefix the property name prefix to be added in front of each
         * property name, must not be {@literal null}.
         * @return a new {@link KeyPrefixPropertyTransformer} that adds a prefix to each
         * key name.
         */
        public static PropertyTransformer forPrefix(String propertyNamePrefix) {
            return new KeyPrefixPropertyTransformer(propertyNamePrefix);
        }

        @Override
        public Map<String, Object> transformProperties(Map<String, ? extends Object> input) {

            Map<String, Object> target = new LinkedHashMap<>(input.size(), 1);

            for (Map.Entry<String, ? extends Object> entry : input.entrySet()) {
                target.put(this.propertyNamePrefix + entry.getKey(), entry.getValue());
            }

            return target;
        }

    }

}

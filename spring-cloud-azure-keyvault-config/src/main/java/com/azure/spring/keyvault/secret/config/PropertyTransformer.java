package com.azure.spring.keyvault.secret.config;


import java.util.Map;

/**
 * Strategy interface to transform properties to a new key-value {@link Map} in a
 * functional style. Property transformation can remap property names, adjust values or
 * change the property map entirely without changing the input.
 * <p>
 * Implementors usually transform property names to target property names by retaining the
 * value.
 *
 * @author Mark Paluch
 */
@FunctionalInterface
public interface PropertyTransformer {

    /**
     * Transform properties by creating a new map using the transformed property set.
     * <p>
     * Implementing classes do not change the {@code input} but create a new {@link Map
     * property map}.
     * @param input must not be {@literal null}.
     * @return transformed properties.
     */
    Map<String, Object> transformProperties(Map<String, ? extends Object> input);

    /**
     * Return a composed transformer function that first applies this filter, and then
     * applies the {@code after} transformer.
     * @param after the transformer to apply after this transformer is applied.
     * @return a composed transformer that first applies this function and then applies
     * the {@code after} transformer.
     */
    default PropertyTransformer andThen(PropertyTransformer after) {
        return input -> after.transformProperties(transformProperties(input));
    }

}

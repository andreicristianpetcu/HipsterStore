package net.petcu.store.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class PriceAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPriceAllPropertiesEquals(Price expected, Price actual) {
        assertPriceAutoGeneratedPropertiesEquals(expected, actual);
        assertPriceAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPriceAllUpdatablePropertiesEquals(Price expected, Price actual) {
        assertPriceUpdatableFieldsEquals(expected, actual);
        assertPriceUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPriceAutoGeneratedPropertiesEquals(Price expected, Price actual) {
        assertThat(actual)
            .as("Verify Price auto generated properties")
            .satisfies(a -> assertThat(a.getId()).as("check id").isEqualTo(expected.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPriceUpdatableFieldsEquals(Price expected, Price actual) {
        assertThat(actual)
            .as("Verify Price relevant properties")
            .satisfies(a -> assertThat(a.getValue()).as("check value").isEqualTo(expected.getValue()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPriceUpdatableRelationshipsEquals(Price expected, Price actual) {
        // empty method
    }
}

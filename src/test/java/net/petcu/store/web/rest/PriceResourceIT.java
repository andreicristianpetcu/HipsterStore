package net.petcu.store.web.rest;

import static net.petcu.store.domain.PriceAsserts.*;
import static net.petcu.store.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import net.petcu.store.IntegrationTest;
import net.petcu.store.domain.Price;
import net.petcu.store.repository.PriceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PriceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PriceResourceIT {

    private static final Double DEFAULT_VALUE = 1D;
    private static final Double UPDATED_VALUE = 2D;

    private static final String ENTITY_API_URL = "/api/prices";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPriceMockMvc;

    private Price price;

    private Price insertedPrice;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Price createEntity() {
        return new Price().value(DEFAULT_VALUE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Price createUpdatedEntity() {
        return new Price().value(UPDATED_VALUE);
    }

    @BeforeEach
    public void initTest() {
        price = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedPrice != null) {
            priceRepository.delete(insertedPrice);
            insertedPrice = null;
        }
    }

    @Test
    @Transactional
    void createPrice() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Price
        var returnedPrice = om.readValue(
            restPriceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(price)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Price.class
        );

        // Validate the Price in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertPriceUpdatableFieldsEquals(returnedPrice, getPersistedPrice(returnedPrice));

        insertedPrice = returnedPrice;
    }

    @Test
    @Transactional
    void createPriceWithExistingId() throws Exception {
        // Create the Price with an existing ID
        price.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPriceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(price)))
            .andExpect(status().isBadRequest());

        // Validate the Price in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPrices() throws Exception {
        // Initialize the database
        insertedPrice = priceRepository.saveAndFlush(price);

        // Get all the priceList
        restPriceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(price.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }

    @Test
    @Transactional
    void getPrice() throws Exception {
        // Initialize the database
        insertedPrice = priceRepository.saveAndFlush(price);

        // Get the price
        restPriceMockMvc
            .perform(get(ENTITY_API_URL_ID, price.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(price.getId().intValue()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE));
    }

    @Test
    @Transactional
    void getNonExistingPrice() throws Exception {
        // Get the price
        restPriceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPrice() throws Exception {
        // Initialize the database
        insertedPrice = priceRepository.saveAndFlush(price);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the price
        Price updatedPrice = priceRepository.findById(price.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPrice are not directly saved in db
        em.detach(updatedPrice);
        updatedPrice.value(UPDATED_VALUE);

        restPriceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPrice.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedPrice))
            )
            .andExpect(status().isOk());

        // Validate the Price in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPriceToMatchAllProperties(updatedPrice);
    }

    @Test
    @Transactional
    void putNonExistingPrice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        price.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPriceMockMvc
            .perform(put(ENTITY_API_URL_ID, price.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(price)))
            .andExpect(status().isBadRequest());

        // Validate the Price in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPrice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        price.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPriceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(price))
            )
            .andExpect(status().isBadRequest());

        // Validate the Price in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPrice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        price.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPriceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(price)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Price in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePriceWithPatch() throws Exception {
        // Initialize the database
        insertedPrice = priceRepository.saveAndFlush(price);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the price using partial update
        Price partialUpdatedPrice = new Price();
        partialUpdatedPrice.setId(price.getId());

        partialUpdatedPrice.value(UPDATED_VALUE);

        restPriceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPrice.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPrice))
            )
            .andExpect(status().isOk());

        // Validate the Price in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPriceUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPrice, price), getPersistedPrice(price));
    }

    @Test
    @Transactional
    void fullUpdatePriceWithPatch() throws Exception {
        // Initialize the database
        insertedPrice = priceRepository.saveAndFlush(price);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the price using partial update
        Price partialUpdatedPrice = new Price();
        partialUpdatedPrice.setId(price.getId());

        partialUpdatedPrice.value(UPDATED_VALUE);

        restPriceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPrice.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPrice))
            )
            .andExpect(status().isOk());

        // Validate the Price in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPriceUpdatableFieldsEquals(partialUpdatedPrice, getPersistedPrice(partialUpdatedPrice));
    }

    @Test
    @Transactional
    void patchNonExistingPrice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        price.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPriceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, price.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(price))
            )
            .andExpect(status().isBadRequest());

        // Validate the Price in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPrice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        price.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPriceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(price))
            )
            .andExpect(status().isBadRequest());

        // Validate the Price in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPrice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        price.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPriceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(price)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Price in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePrice() throws Exception {
        // Initialize the database
        insertedPrice = priceRepository.saveAndFlush(price);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the price
        restPriceMockMvc
            .perform(delete(ENTITY_API_URL_ID, price.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return priceRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Price getPersistedPrice(Price price) {
        return priceRepository.findById(price.getId()).orElseThrow();
    }

    protected void assertPersistedPriceToMatchAllProperties(Price expectedPrice) {
        assertPriceAllPropertiesEquals(expectedPrice, getPersistedPrice(expectedPrice));
    }

    protected void assertPersistedPriceToMatchUpdatableProperties(Price expectedPrice) {
        assertPriceAllUpdatablePropertiesEquals(expectedPrice, getPersistedPrice(expectedPrice));
    }
}

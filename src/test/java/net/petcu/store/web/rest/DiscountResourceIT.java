package net.petcu.store.web.rest;

import static net.petcu.store.domain.DiscountAsserts.*;
import static net.petcu.store.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import net.petcu.store.IntegrationTest;
import net.petcu.store.domain.Discount;
import net.petcu.store.domain.enumeration.DiscountType;
import net.petcu.store.repository.DiscountRepository;
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
 * Integration tests for the {@link DiscountResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DiscountResourceIT {

    private static final UUID DEFAULT_DISCOUNT_CODE = UUID.randomUUID();
    private static final UUID UPDATED_DISCOUNT_CODE = UUID.randomUUID();

    private static final DiscountType DEFAULT_DISCOUNT_TYPE = DiscountType.PERCENTAGE;
    private static final DiscountType UPDATED_DISCOUNT_TYPE = DiscountType.FIXED;

    private static final Boolean DEFAULT_USED = false;
    private static final Boolean UPDATED_USED = true;

    private static final String ENTITY_API_URL = "/api/discounts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDiscountMockMvc;

    private Discount discount;

    private Discount insertedDiscount;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Discount createEntity() {
        return new Discount().discountCode(DEFAULT_DISCOUNT_CODE).discountType(DEFAULT_DISCOUNT_TYPE).used(DEFAULT_USED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Discount createUpdatedEntity() {
        return new Discount().discountCode(UPDATED_DISCOUNT_CODE).discountType(UPDATED_DISCOUNT_TYPE).used(UPDATED_USED);
    }

    @BeforeEach
    public void initTest() {
        discount = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedDiscount != null) {
            discountRepository.delete(insertedDiscount);
            insertedDiscount = null;
        }
    }

    @Test
    @Transactional
    void createDiscount() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Discount
        var returnedDiscount = om.readValue(
            restDiscountMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(discount)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Discount.class
        );

        // Validate the Discount in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertDiscountUpdatableFieldsEquals(returnedDiscount, getPersistedDiscount(returnedDiscount));

        insertedDiscount = returnedDiscount;
    }

    @Test
    @Transactional
    void createDiscountWithExistingId() throws Exception {
        // Create the Discount with an existing ID
        discount.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDiscountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(discount)))
            .andExpect(status().isBadRequest());

        // Validate the Discount in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDiscounts() throws Exception {
        // Initialize the database
        insertedDiscount = discountRepository.saveAndFlush(discount);

        // Get all the discountList
        restDiscountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(discount.getId().intValue())))
            .andExpect(jsonPath("$.[*].discountCode").value(hasItem(DEFAULT_DISCOUNT_CODE.toString())))
            .andExpect(jsonPath("$.[*].discountType").value(hasItem(DEFAULT_DISCOUNT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].used").value(hasItem(DEFAULT_USED)));
    }

    @Test
    @Transactional
    void getDiscount() throws Exception {
        // Initialize the database
        insertedDiscount = discountRepository.saveAndFlush(discount);

        // Get the discount
        restDiscountMockMvc
            .perform(get(ENTITY_API_URL_ID, discount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(discount.getId().intValue()))
            .andExpect(jsonPath("$.discountCode").value(DEFAULT_DISCOUNT_CODE.toString()))
            .andExpect(jsonPath("$.discountType").value(DEFAULT_DISCOUNT_TYPE.toString()))
            .andExpect(jsonPath("$.used").value(DEFAULT_USED));
    }

    @Test
    @Transactional
    void getNonExistingDiscount() throws Exception {
        // Get the discount
        restDiscountMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDiscount() throws Exception {
        // Initialize the database
        insertedDiscount = discountRepository.saveAndFlush(discount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the discount
        Discount updatedDiscount = discountRepository.findById(discount.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDiscount are not directly saved in db
        em.detach(updatedDiscount);
        updatedDiscount.discountCode(UPDATED_DISCOUNT_CODE).discountType(UPDATED_DISCOUNT_TYPE).used(UPDATED_USED);

        restDiscountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDiscount.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedDiscount))
            )
            .andExpect(status().isOk());

        // Validate the Discount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDiscountToMatchAllProperties(updatedDiscount);
    }

    @Test
    @Transactional
    void putNonExistingDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discount.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDiscountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, discount.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(discount))
            )
            .andExpect(status().isBadRequest());

        // Validate the Discount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discount.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiscountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(discount))
            )
            .andExpect(status().isBadRequest());

        // Validate the Discount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discount.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiscountMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(discount)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Discount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDiscountWithPatch() throws Exception {
        // Initialize the database
        insertedDiscount = discountRepository.saveAndFlush(discount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the discount using partial update
        Discount partialUpdatedDiscount = new Discount();
        partialUpdatedDiscount.setId(discount.getId());

        partialUpdatedDiscount.discountType(UPDATED_DISCOUNT_TYPE).used(UPDATED_USED);

        restDiscountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDiscount.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDiscount))
            )
            .andExpect(status().isOk());

        // Validate the Discount in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDiscountUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDiscount, discount), getPersistedDiscount(discount));
    }

    @Test
    @Transactional
    void fullUpdateDiscountWithPatch() throws Exception {
        // Initialize the database
        insertedDiscount = discountRepository.saveAndFlush(discount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the discount using partial update
        Discount partialUpdatedDiscount = new Discount();
        partialUpdatedDiscount.setId(discount.getId());

        partialUpdatedDiscount.discountCode(UPDATED_DISCOUNT_CODE).discountType(UPDATED_DISCOUNT_TYPE).used(UPDATED_USED);

        restDiscountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDiscount.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDiscount))
            )
            .andExpect(status().isOk());

        // Validate the Discount in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDiscountUpdatableFieldsEquals(partialUpdatedDiscount, getPersistedDiscount(partialUpdatedDiscount));
    }

    @Test
    @Transactional
    void patchNonExistingDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discount.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDiscountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, discount.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(discount))
            )
            .andExpect(status().isBadRequest());

        // Validate the Discount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discount.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiscountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(discount))
            )
            .andExpect(status().isBadRequest());

        // Validate the Discount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discount.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiscountMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(discount)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Discount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDiscount() throws Exception {
        // Initialize the database
        insertedDiscount = discountRepository.saveAndFlush(discount);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the discount
        restDiscountMockMvc
            .perform(delete(ENTITY_API_URL_ID, discount.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return discountRepository.count();
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

    protected Discount getPersistedDiscount(Discount discount) {
        return discountRepository.findById(discount.getId()).orElseThrow();
    }

    protected void assertPersistedDiscountToMatchAllProperties(Discount expectedDiscount) {
        assertDiscountAllPropertiesEquals(expectedDiscount, getPersistedDiscount(expectedDiscount));
    }

    protected void assertPersistedDiscountToMatchUpdatableProperties(Discount expectedDiscount) {
        assertDiscountAllUpdatablePropertiesEquals(expectedDiscount, getPersistedDiscount(expectedDiscount));
    }
}

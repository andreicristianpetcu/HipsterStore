package net.petcu.store.web.rest;

import static net.petcu.store.domain.PricedProductAsserts.*;
import static net.petcu.store.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import net.petcu.store.IntegrationTest;
import net.petcu.store.domain.PricedProduct;
import net.petcu.store.repository.PricedProductRepository;
import net.petcu.store.service.PricedProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PricedProductResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PricedProductResourceIT {

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Instant DEFAULT_UPDATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/priced-products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PricedProductRepository pricedProductRepository;

    @Mock
    private PricedProductRepository pricedProductRepositoryMock;

    @Mock
    private PricedProductService pricedProductServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPricedProductMockMvc;

    private PricedProduct pricedProduct;

    private PricedProduct insertedPricedProduct;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PricedProduct createEntity() {
        return new PricedProduct().active(DEFAULT_ACTIVE).updatedDate(DEFAULT_UPDATED_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PricedProduct createUpdatedEntity() {
        return new PricedProduct().active(UPDATED_ACTIVE).updatedDate(UPDATED_UPDATED_DATE);
    }

    @BeforeEach
    public void initTest() {
        pricedProduct = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedPricedProduct != null) {
            pricedProductRepository.delete(insertedPricedProduct);
            insertedPricedProduct = null;
        }
    }

    @Test
    @Transactional
    void createPricedProduct() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PricedProduct
        var returnedPricedProduct = om.readValue(
            restPricedProductMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pricedProduct)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PricedProduct.class
        );

        // Validate the PricedProduct in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertPricedProductUpdatableFieldsEquals(returnedPricedProduct, getPersistedPricedProduct(returnedPricedProduct));

        insertedPricedProduct = returnedPricedProduct;
    }

    @Test
    @Transactional
    void createPricedProductWithExistingId() throws Exception {
        // Create the PricedProduct with an existing ID
        pricedProduct.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPricedProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pricedProduct)))
            .andExpect(status().isBadRequest());

        // Validate the PricedProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPricedProducts() throws Exception {
        // Initialize the database
        insertedPricedProduct = pricedProductRepository.saveAndFlush(pricedProduct);

        // Get all the pricedProductList
        restPricedProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pricedProduct.getId().intValue())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].updatedDate").value(hasItem(DEFAULT_UPDATED_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPricedProductsWithEagerRelationshipsIsEnabled() throws Exception {
        when(pricedProductServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPricedProductMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(pricedProductServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPricedProductsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(pricedProductServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPricedProductMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(pricedProductRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPricedProduct() throws Exception {
        // Initialize the database
        insertedPricedProduct = pricedProductRepository.saveAndFlush(pricedProduct);

        // Get the pricedProduct
        restPricedProductMockMvc
            .perform(get(ENTITY_API_URL_ID, pricedProduct.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pricedProduct.getId().intValue()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
            .andExpect(jsonPath("$.updatedDate").value(DEFAULT_UPDATED_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPricedProduct() throws Exception {
        // Get the pricedProduct
        restPricedProductMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPricedProduct() throws Exception {
        // Initialize the database
        insertedPricedProduct = pricedProductRepository.saveAndFlush(pricedProduct);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pricedProduct
        PricedProduct updatedPricedProduct = pricedProductRepository.findById(pricedProduct.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPricedProduct are not directly saved in db
        em.detach(updatedPricedProduct);
        updatedPricedProduct.active(UPDATED_ACTIVE).updatedDate(UPDATED_UPDATED_DATE);

        restPricedProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPricedProduct.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedPricedProduct))
            )
            .andExpect(status().isOk());

        // Validate the PricedProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPricedProductToMatchAllProperties(updatedPricedProduct);
    }

    @Test
    @Transactional
    void putNonExistingPricedProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pricedProduct.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPricedProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pricedProduct.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pricedProduct))
            )
            .andExpect(status().isBadRequest());

        // Validate the PricedProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPricedProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pricedProduct.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPricedProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pricedProduct))
            )
            .andExpect(status().isBadRequest());

        // Validate the PricedProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPricedProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pricedProduct.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPricedProductMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pricedProduct)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PricedProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePricedProductWithPatch() throws Exception {
        // Initialize the database
        insertedPricedProduct = pricedProductRepository.saveAndFlush(pricedProduct);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pricedProduct using partial update
        PricedProduct partialUpdatedPricedProduct = new PricedProduct();
        partialUpdatedPricedProduct.setId(pricedProduct.getId());

        partialUpdatedPricedProduct.active(UPDATED_ACTIVE);

        restPricedProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPricedProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPricedProduct))
            )
            .andExpect(status().isOk());

        // Validate the PricedProduct in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPricedProductUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPricedProduct, pricedProduct),
            getPersistedPricedProduct(pricedProduct)
        );
    }

    @Test
    @Transactional
    void fullUpdatePricedProductWithPatch() throws Exception {
        // Initialize the database
        insertedPricedProduct = pricedProductRepository.saveAndFlush(pricedProduct);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pricedProduct using partial update
        PricedProduct partialUpdatedPricedProduct = new PricedProduct();
        partialUpdatedPricedProduct.setId(pricedProduct.getId());

        partialUpdatedPricedProduct.active(UPDATED_ACTIVE).updatedDate(UPDATED_UPDATED_DATE);

        restPricedProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPricedProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPricedProduct))
            )
            .andExpect(status().isOk());

        // Validate the PricedProduct in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPricedProductUpdatableFieldsEquals(partialUpdatedPricedProduct, getPersistedPricedProduct(partialUpdatedPricedProduct));
    }

    @Test
    @Transactional
    void patchNonExistingPricedProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pricedProduct.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPricedProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pricedProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pricedProduct))
            )
            .andExpect(status().isBadRequest());

        // Validate the PricedProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPricedProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pricedProduct.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPricedProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pricedProduct))
            )
            .andExpect(status().isBadRequest());

        // Validate the PricedProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPricedProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pricedProduct.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPricedProductMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(pricedProduct)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PricedProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePricedProduct() throws Exception {
        // Initialize the database
        insertedPricedProduct = pricedProductRepository.saveAndFlush(pricedProduct);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the pricedProduct
        restPricedProductMockMvc
            .perform(delete(ENTITY_API_URL_ID, pricedProduct.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return pricedProductRepository.count();
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

    protected PricedProduct getPersistedPricedProduct(PricedProduct pricedProduct) {
        return pricedProductRepository.findById(pricedProduct.getId()).orElseThrow();
    }

    protected void assertPersistedPricedProductToMatchAllProperties(PricedProduct expectedPricedProduct) {
        assertPricedProductAllPropertiesEquals(expectedPricedProduct, getPersistedPricedProduct(expectedPricedProduct));
    }

    protected void assertPersistedPricedProductToMatchUpdatableProperties(PricedProduct expectedPricedProduct) {
        assertPricedProductAllUpdatablePropertiesEquals(expectedPricedProduct, getPersistedPricedProduct(expectedPricedProduct));
    }
}

package net.petcu.store.service;

import java.util.List;
import java.util.Optional;
import net.petcu.store.domain.PricedProduct;
import net.petcu.store.repository.PricedProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link net.petcu.store.domain.PricedProduct}.
 */
@Service
@Transactional
public class PricedProductService {

    private static final Logger LOG = LoggerFactory.getLogger(PricedProductService.class);

    private final PricedProductRepository pricedProductRepository;

    public PricedProductService(PricedProductRepository pricedProductRepository) {
        this.pricedProductRepository = pricedProductRepository;
    }

    /**
     * Save a pricedProduct.
     *
     * @param pricedProduct the entity to save.
     * @return the persisted entity.
     */
    public PricedProduct save(PricedProduct pricedProduct) {
        LOG.debug("Request to save PricedProduct : {}", pricedProduct);
        return pricedProductRepository.save(pricedProduct);
    }

    /**
     * Update a pricedProduct.
     *
     * @param pricedProduct the entity to save.
     * @return the persisted entity.
     */
    public PricedProduct update(PricedProduct pricedProduct) {
        LOG.debug("Request to update PricedProduct : {}", pricedProduct);
        return pricedProductRepository.save(pricedProduct);
    }

    /**
     * Partially update a pricedProduct.
     *
     * @param pricedProduct the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PricedProduct> partialUpdate(PricedProduct pricedProduct) {
        LOG.debug("Request to partially update PricedProduct : {}", pricedProduct);

        return pricedProductRepository
            .findById(pricedProduct.getId())
            .map(existingPricedProduct -> {
                if (pricedProduct.getActive() != null) {
                    existingPricedProduct.setActive(pricedProduct.getActive());
                }
                if (pricedProduct.getUpdatedDate() != null) {
                    existingPricedProduct.setUpdatedDate(pricedProduct.getUpdatedDate());
                }

                return existingPricedProduct;
            })
            .map(pricedProductRepository::save);
    }

    /**
     * Get all the pricedProducts.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PricedProduct> findAll() {
        LOG.debug("Request to get all PricedProducts");
        return pricedProductRepository.findAll();
    }

    /**
     * Get all the pricedProducts with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<PricedProduct> findAllWithEagerRelationships(Pageable pageable) {
        return pricedProductRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one pricedProduct by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PricedProduct> findOne(Long id) {
        LOG.debug("Request to get PricedProduct : {}", id);
        return pricedProductRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the pricedProduct by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PricedProduct : {}", id);
        pricedProductRepository.deleteById(id);
    }
}

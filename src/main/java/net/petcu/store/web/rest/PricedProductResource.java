package net.petcu.store.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.petcu.store.domain.PricedProduct;
import net.petcu.store.repository.PricedProductRepository;
import net.petcu.store.service.PricedProductService;
import net.petcu.store.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link net.petcu.store.domain.PricedProduct}.
 */
@RestController
@RequestMapping("/api/priced-products")
public class PricedProductResource {

    private static final Logger LOG = LoggerFactory.getLogger(PricedProductResource.class);

    private static final String ENTITY_NAME = "pricedProduct";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PricedProductService pricedProductService;

    private final PricedProductRepository pricedProductRepository;

    public PricedProductResource(PricedProductService pricedProductService, PricedProductRepository pricedProductRepository) {
        this.pricedProductService = pricedProductService;
        this.pricedProductRepository = pricedProductRepository;
    }

    /**
     * {@code POST  /priced-products} : Create a new pricedProduct.
     *
     * @param pricedProduct the pricedProduct to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pricedProduct, or with status {@code 400 (Bad Request)} if the pricedProduct has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PricedProduct> createPricedProduct(@RequestBody PricedProduct pricedProduct) throws URISyntaxException {
        LOG.debug("REST request to save PricedProduct : {}", pricedProduct);
        if (pricedProduct.getId() != null) {
            throw new BadRequestAlertException("A new pricedProduct cannot already have an ID", ENTITY_NAME, "idexists");
        }
        pricedProduct = pricedProductService.save(pricedProduct);
        return ResponseEntity.created(new URI("/api/priced-products/" + pricedProduct.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, pricedProduct.getId().toString()))
            .body(pricedProduct);
    }

    /**
     * {@code PUT  /priced-products/:id} : Updates an existing pricedProduct.
     *
     * @param id the id of the pricedProduct to save.
     * @param pricedProduct the pricedProduct to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pricedProduct,
     * or with status {@code 400 (Bad Request)} if the pricedProduct is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pricedProduct couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PricedProduct> updatePricedProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PricedProduct pricedProduct
    ) throws URISyntaxException {
        LOG.debug("REST request to update PricedProduct : {}, {}", id, pricedProduct);
        if (pricedProduct.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pricedProduct.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pricedProductRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        pricedProduct = pricedProductService.update(pricedProduct);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pricedProduct.getId().toString()))
            .body(pricedProduct);
    }

    /**
     * {@code PATCH  /priced-products/:id} : Partial updates given fields of an existing pricedProduct, field will ignore if it is null
     *
     * @param id the id of the pricedProduct to save.
     * @param pricedProduct the pricedProduct to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pricedProduct,
     * or with status {@code 400 (Bad Request)} if the pricedProduct is not valid,
     * or with status {@code 404 (Not Found)} if the pricedProduct is not found,
     * or with status {@code 500 (Internal Server Error)} if the pricedProduct couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PricedProduct> partialUpdatePricedProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PricedProduct pricedProduct
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PricedProduct partially : {}, {}", id, pricedProduct);
        if (pricedProduct.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pricedProduct.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pricedProductRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PricedProduct> result = pricedProductService.partialUpdate(pricedProduct);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pricedProduct.getId().toString())
        );
    }

    /**
     * {@code GET  /priced-products} : get all the pricedProducts.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pricedProducts in body.
     */
    @GetMapping("")
    public List<PricedProduct> getAllPricedProducts(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all PricedProducts");
        return pricedProductService.findAll();
    }

    /**
     * {@code GET  /priced-products/:id} : get the "id" pricedProduct.
     *
     * @param id the id of the pricedProduct to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pricedProduct, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PricedProduct> getPricedProduct(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PricedProduct : {}", id);
        Optional<PricedProduct> pricedProduct = pricedProductService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pricedProduct);
    }

    /**
     * {@code DELETE  /priced-products/:id} : delete the "id" pricedProduct.
     *
     * @param id the id of the pricedProduct to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePricedProduct(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PricedProduct : {}", id);
        pricedProductService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}

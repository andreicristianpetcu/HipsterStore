package net.petcu.store.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.petcu.store.domain.Price;
import net.petcu.store.repository.PriceRepository;
import net.petcu.store.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link net.petcu.store.domain.Price}.
 */
@RestController
@RequestMapping("/api/prices")
@Transactional
public class PriceResource {

    private static final Logger LOG = LoggerFactory.getLogger(PriceResource.class);

    private static final String ENTITY_NAME = "price";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PriceRepository priceRepository;

    public PriceResource(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    /**
     * {@code POST  /prices} : Create a new price.
     *
     * @param price the price to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new price, or with status {@code 400 (Bad Request)} if the price has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Price> createPrice(@RequestBody Price price) throws URISyntaxException {
        LOG.debug("REST request to save Price : {}", price);
        if (price.getId() != null) {
            throw new BadRequestAlertException("A new price cannot already have an ID", ENTITY_NAME, "idexists");
        }
        price = priceRepository.save(price);
        return ResponseEntity.created(new URI("/api/prices/" + price.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, price.getId().toString()))
            .body(price);
    }

    /**
     * {@code PUT  /prices/:id} : Updates an existing price.
     *
     * @param id the id of the price to save.
     * @param price the price to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated price,
     * or with status {@code 400 (Bad Request)} if the price is not valid,
     * or with status {@code 500 (Internal Server Error)} if the price couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Price> updatePrice(@PathVariable(value = "id", required = false) final Long id, @RequestBody Price price)
        throws URISyntaxException {
        LOG.debug("REST request to update Price : {}, {}", id, price);
        if (price.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, price.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!priceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        price = priceRepository.save(price);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, price.getId().toString()))
            .body(price);
    }

    /**
     * {@code PATCH  /prices/:id} : Partial updates given fields of an existing price, field will ignore if it is null
     *
     * @param id the id of the price to save.
     * @param price the price to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated price,
     * or with status {@code 400 (Bad Request)} if the price is not valid,
     * or with status {@code 404 (Not Found)} if the price is not found,
     * or with status {@code 500 (Internal Server Error)} if the price couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Price> partialUpdatePrice(@PathVariable(value = "id", required = false) final Long id, @RequestBody Price price)
        throws URISyntaxException {
        LOG.debug("REST request to partial update Price partially : {}, {}", id, price);
        if (price.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, price.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!priceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Price> result = priceRepository
            .findById(price.getId())
            .map(existingPrice -> {
                if (price.getValue() != null) {
                    existingPrice.setValue(price.getValue());
                }

                return existingPrice;
            })
            .map(priceRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, price.getId().toString())
        );
    }

    /**
     * {@code GET  /prices} : get all the prices.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of prices in body.
     */
    @GetMapping("")
    public List<Price> getAllPrices() {
        LOG.debug("REST request to get all Prices");
        return priceRepository.findAll();
    }

    /**
     * {@code GET  /prices/:id} : get the "id" price.
     *
     * @param id the id of the price to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the price, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Price> getPrice(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Price : {}", id);
        Optional<Price> price = priceRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(price);
    }

    /**
     * {@code DELETE  /prices/:id} : delete the "id" price.
     *
     * @param id the id of the price to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrice(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Price : {}", id);
        priceRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}

package net.petcu.store.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.petcu.store.domain.Price;
import net.petcu.store.domain.PricedProduct;
import net.petcu.store.domain.Product;
import net.petcu.store.exception.ProductNotFoundException;
import net.petcu.store.repository.PricedProductRepository;
import net.petcu.store.repository.ProductRepository;
import net.petcu.store.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);
    private final ProductRepository productRepository;
    private final PricedProductRepository pricedProductRepository;

    @Override
    public PricedProduct changePrice(Long productId, Double newPrice) {
        log.debug("Request to change price for productId={} to newPrice={}", productId, newPrice);

        // Find the product
        Product product = productRepository
            .findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found: ", productId));
        log.debug("Found product productId={} name={}", productId, product.getName());

        List<PricedProduct> activePrices = pricedProductRepository.findByProductIdAndActiveTrue(productId);
        log.debug("Found {} active prices for productId={}", activePrices.size(), productId);

        activePrices.forEach(price -> {
            price.setActive(false);
            log.debug("Deactivating price id={} for productId={}", price.getId(), productId);
        });
        pricedProductRepository.saveAll(activePrices);

        // Create new price
        Price price = new Price();
        price.setValue(newPrice);

        PricedProduct newPricedProduct = new PricedProduct();
        newPricedProduct.setProduct(product);
        newPricedProduct.setPrice(price);
        newPricedProduct.setActive(true);

        newPricedProduct = pricedProductRepository.save(newPricedProduct);
        log.debug("Created new price id={} for productId={} with value={}", newPricedProduct.getId(), productId, newPrice);

        return newPricedProduct;
    }
}

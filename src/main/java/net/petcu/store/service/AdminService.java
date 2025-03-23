package net.petcu.store.service;

import net.petcu.store.domain.PricedProduct;

public interface AdminService {
    PricedProduct changePrice(Long productId, Double newPrice);
}

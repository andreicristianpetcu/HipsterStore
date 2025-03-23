package net.petcu.store.web.rest;

import net.petcu.store.domain.PricedProduct;
import net.petcu.store.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PutMapping("/products/{productId}/price")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<PricedProduct> changePrice(@PathVariable Long productId, @RequestParam Double newPrice) {
        log.debug("REST request to change price for productId={} to newPrice={}", productId, newPrice);

        if (newPrice <= 0) {
            log.warn("Invalid price value {} provided for productId={}", newPrice, productId);
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(adminService.changePrice(productId, newPrice));
    }
}

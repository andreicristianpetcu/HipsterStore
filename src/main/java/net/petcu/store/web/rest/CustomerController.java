package net.petcu.store.web.rest;

import net.petcu.store.service.CustomerService;
import net.petcu.store.service.dto.OrderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final Logger log = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/orders")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<OrderDTO> createOrder() {
        log.debug("REST request to create a new order");
        OrderDTO result = customerService.createOrder();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/orders/{orderId}/items")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<OrderDTO> addItemToOrder(
        @PathVariable Long orderId,
        @RequestParam Long productId,
        @RequestParam(defaultValue = "1") Integer quantity
    ) {
        log.debug("REST request to add item to order : {}", orderId);
        OrderDTO result = customerService.addItemToOrder(orderId, productId, quantity);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/orders/{orderId}/discount")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<OrderDTO> applyDiscountCode(@PathVariable Long orderId, @RequestParam String discountCode) {
        log.debug("REST request to apply discount code to order : {}", orderId);
        OrderDTO result = customerService.applyDiscountCode(orderId, discountCode);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/orders/{orderId}/finalize")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<OrderDTO> finalizeOrder(@PathVariable Long orderId) {
        log.debug("REST request to finalize order : {}", orderId);
        OrderDTO result = customerService.finalizeOrder(orderId);
        return ResponseEntity.ok(result);
    }
}

package net.petcu.store.repository;

import java.util.List;
import java.util.UUID;
import net.petcu.store.domain.Discount;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Discount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    List<Discount> findByDiscountCode(UUID discountCode);
}

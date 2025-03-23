package net.petcu.store.repository;

import java.util.List;
import java.util.Optional;
import net.petcu.store.domain.PricedProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PricedProduct entity.
 */
@Repository
public interface PricedProductRepository extends JpaRepository<PricedProduct, Long> {
    default Optional<PricedProduct> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<PricedProduct> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<PricedProduct> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select pricedProduct from PricedProduct pricedProduct left join fetch pricedProduct.product left join fetch pricedProduct.price",
        countQuery = "select count(pricedProduct) from PricedProduct pricedProduct"
    )
    Page<PricedProduct> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select pricedProduct from PricedProduct pricedProduct left join fetch pricedProduct.product left join fetch pricedProduct.price"
    )
    List<PricedProduct> findAllWithToOneRelationships();

    @Query(
        "select pricedProduct from PricedProduct pricedProduct left join fetch pricedProduct.product left join fetch pricedProduct.price where pricedProduct.id =:id"
    )
    Optional<PricedProduct> findOneWithToOneRelationships(@Param("id") Long id);

    Object findLatestActiveByProductId(Long defaultProductId);
}

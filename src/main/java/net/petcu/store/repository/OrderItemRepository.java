package net.petcu.store.repository;

import java.util.List;
import java.util.Optional;
import net.petcu.store.domain.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OrderItem entity.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    default Optional<OrderItem> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<OrderItem> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<OrderItem> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select orderItem from OrderItem orderItem left join fetch orderItem.product left join fetch orderItem.price",
        countQuery = "select count(orderItem) from OrderItem orderItem"
    )
    Page<OrderItem> findAllWithToOneRelationships(Pageable pageable);

    @Query("select orderItem from OrderItem orderItem left join fetch orderItem.product left join fetch orderItem.price")
    List<OrderItem> findAllWithToOneRelationships();

    @Query(
        "select orderItem from OrderItem orderItem left join fetch orderItem.product left join fetch orderItem.price where orderItem.id =:id"
    )
    Optional<OrderItem> findOneWithToOneRelationships(@Param("id") Long id);
}

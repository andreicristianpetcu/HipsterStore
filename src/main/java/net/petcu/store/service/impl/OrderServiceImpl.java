package net.petcu.store.service.impl;

import java.util.List;
import java.util.Optional;
import net.petcu.store.domain.Order;
import net.petcu.store.repository.OrderRepository;
import net.petcu.store.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link net.petcu.store.domain.Order}.
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order save(Order order) {
        LOG.debug("Request to save Order : {}", order);
        return orderRepository.save(order);
    }

    @Override
    public Order update(Order order) {
        LOG.debug("Request to update Order : {}", order);
        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> partialUpdate(Order order) {
        LOG.debug("Request to partially update Order : {}", order);

        return orderRepository
            .findById(order.getId())
            .map(existingOrder -> {
                if (order.getDate() != null) {
                    existingOrder.setDate(order.getDate());
                }
                if (order.getSubtotal() != null) {
                    existingOrder.setSubtotal(order.getSubtotal());
                }
                if (order.getFinalPrice() != null) {
                    existingOrder.setFinalPrice(order.getFinalPrice());
                }
                if (order.getStatus() != null) {
                    existingOrder.setStatus(order.getStatus());
                }

                return existingOrder;
            })
            .map(orderRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findAll() {
        LOG.debug("Request to get all Orders");
        return orderRepository.findAll();
    }

    public Page<Order> findAllWithEagerRelationships(Pageable pageable) {
        return orderRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findOne(Long id) {
        LOG.debug("Request to get Order : {}", id);
        return orderRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Order : {}", id);
        orderRepository.deleteById(id);
    }
}

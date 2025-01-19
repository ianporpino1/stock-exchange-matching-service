package com.stockexchange.matchingservice.repository;

import com.stockexchange.matchingservice.model.Order;
import com.stockexchange.matchingservice.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query("SELECT o FROM Order o WHERE o.symbol = :symbol " +
            "AND o.status IN (:accepted, :partiallyExecuted)")
    List<Order> findOpenOrders(@Param("symbol") String symbol,
                                                      @Param("accepted") OrderStatus accepted,
                                                      @Param("partiallyExecuted") OrderStatus partiallyExecuted);

}


package rarlog.me.kitchen.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import rarlog.me.kitchen.entity.OrderItem;
import rarlog.me.kitchen.entity.OrderItemId;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {

    List<OrderItem> findByIdOrderId(int orderId);

}

package rarlog.me.kitchen.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import rarlog.me.kitchen.entity.CustomerOrder;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Integer> {

}

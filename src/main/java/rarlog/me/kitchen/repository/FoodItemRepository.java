package rarlog.me.kitchen.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import rarlog.me.kitchen.entity.FoodItem;

public interface FoodItemRepository extends JpaRepository<FoodItem, Integer> {

}

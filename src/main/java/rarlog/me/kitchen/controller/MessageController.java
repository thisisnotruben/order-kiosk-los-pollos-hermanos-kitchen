package rarlog.me.kitchen.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import rarlog.me.kitchen.dto.FoodItemStatusDto;
import rarlog.me.kitchen.dto.OrderDto;
import rarlog.me.kitchen.dto.OrderItemDto;
import rarlog.me.kitchen.dto.OrderReceivedDto;
import rarlog.me.kitchen.dto.OrderStatusDto;
import rarlog.me.kitchen.entity.CustomerOrder;
import rarlog.me.kitchen.entity.FoodItem;
import rarlog.me.kitchen.entity.OrderItem;
import rarlog.me.kitchen.entity.OrderItemId;
import rarlog.me.kitchen.kitchen.Kitchen;
import rarlog.me.kitchen.repository.CustomerOrderRepository;
import rarlog.me.kitchen.repository.FoodItemRepository;
import rarlog.me.kitchen.repository.OrderItemRepository;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MessageController {

    private final CustomerOrderRepository customerOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final FoodItemRepository foodItemRepository;

    private final Kitchen kitchen;

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);

    @PostMapping("/order")
    public ResponseEntity<OrderReceivedDto> recieveOrder(@RequestBody() OrderDto order) {
        LOGGER.info(String.format("Recieved order: %s", order));

        CustomerOrder customerOrder = CustomerOrder.builder()
                .dateTime(LocalDateTime.now())
                .status(CustomerOrder.Status.PREPARING.toString()).build();
        customerOrderRepository.save(customerOrder);

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemDto orderItemDto : order.getOrder()) {
            FoodItem foodItem = foodItemRepository.findById(orderItemDto.getFoodId()).get();

            orderItems.add(OrderItem.builder()
                    .id(new OrderItemId(customerOrder.getId(), foodItem.getId()))
                    .quantity(orderItemDto.getQuantity())
                    .quantityMade(0)
                    .cost(foodItem.getCost().multiply(BigDecimal.valueOf(orderItemDto.getQuantity())))
                    .status(CustomerOrder.Status.PREPARING.toString()).build());
        }

        orderItemRepository.saveAll(orderItems);
        kitchen.prepareOrder(customerOrder);
        return ResponseEntity.ok(new OrderReceivedDto(customerOrder.getId()));
    }

    @GetMapping("/orderStatus")
    public ResponseEntity<OrderStatusDto> orderStatus(@RequestParam("id") int id) {
        Optional<CustomerOrder> customerOrder = customerOrderRepository.findById(id);
        if (customerOrder.isPresent()) {

            OrderStatusDto.OrderStatusDtoBuilder orderStatusDtoBuilder = OrderStatusDto.builder().orderId(id)
                    .status(customerOrder.get().getStatus().toString());

            List<FoodItemStatusDto> foodItemStatusDtos = new ArrayList<>();
            List<OrderItem> orderItems = orderItemRepository.findByIdOrderId(id);
            for (OrderItem orderItem : orderItems) {

                Optional<FoodItem> foodItem = foodItemRepository.findById(orderItem.getId().getFoodId());
                FoodItemStatusDto.FoodItemStatusDtoBuilder foodItemStatusDtoBuilder = FoodItemStatusDto.builder()
                        .quantity(orderItem.getQuantity())
                        .status(orderItem.getStatus().toString());

                if (foodItem.isPresent()) {
                    foodItemStatusDtoBuilder.foodName(foodItem.get().getName());
                } else {
                    foodItemStatusDtoBuilder.foodName("Unknown");
                }
                foodItemStatusDtos.add(foodItemStatusDtoBuilder.build());
            }

            orderStatusDtoBuilder.foodStatus(foodItemStatusDtos);
            return ResponseEntity.ok(orderStatusDtoBuilder.build());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/getMenu")
    public ResponseEntity<List<FoodItem>> getFoodItems() {
        return ResponseEntity.ok(foodItemRepository.findAll());
    }

}

package rarlog.me.kitchen.dto;

import java.util.List;

import lombok.Data;

@Data
public class OrderDto {

    private List<OrderItemDto> order;

}

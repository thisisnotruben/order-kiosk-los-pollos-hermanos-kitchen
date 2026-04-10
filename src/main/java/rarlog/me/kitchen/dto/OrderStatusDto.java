package rarlog.me.kitchen.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderStatusDto {

    private int orderId;
    private String status;
    private List<FoodItemStatusDto> foodStatus;

}

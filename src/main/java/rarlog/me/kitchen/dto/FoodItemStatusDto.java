package rarlog.me.kitchen.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FoodItemStatusDto {
    
    private String foodName;
    private int quantity;
    private String status;

}

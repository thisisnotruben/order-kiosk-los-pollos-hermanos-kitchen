package rarlog.me.kitchen.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@AllArgsConstructor
public class OrderItemId implements Serializable {

    @Getter
    @Setter
    @Column(name = "order_id")
    private int orderId;

    @Getter
    @Setter
    @Column(name = "food_id")
    private int foodId;

}

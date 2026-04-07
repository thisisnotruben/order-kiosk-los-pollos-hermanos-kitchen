package rarlog.me.kitchen.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "order_item")
public class OrderItem {

    @EmbeddedId
    private OrderItemId id;

    private int quantity;

    @Column(name = "quantity_made")
    private int quantityMade;

    private BigDecimal cost;

    @Column(name = "oi_status")
    private String status;

}

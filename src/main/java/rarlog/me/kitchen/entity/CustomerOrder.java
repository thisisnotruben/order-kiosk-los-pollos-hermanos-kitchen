package rarlog.me.kitchen.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "customer_order")
public class CustomerOrder {

    public enum Status {
        PREPARING,
        READY,
    }

    @Id
    private int id;

    @Column(name = "o_datetime")
    private LocalDateTime dateTime;

    @Column(name = "o_status")
    private String status;

}

package rarlog.me.kitchen.entity;

import java.math.BigDecimal;
import java.util.Random;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "food_item")
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "fi_name")
    private String name;

    @Column(name = "fi_description")
    private String description;

    private String category;

    private BigDecimal cost;

    @Column(name = "preperation_time_sec_min")
    private int preperationTimeSecMin;

    @Column(name = "preperation_time_sec_max")
    private int preperationTimeSecMax;

    public int getPreperationTime() {
        Random random = new Random();
        return random.nextInt(getPreperationTimeSecMax() - getPreperationTimeSecMin())
                + getPreperationTimeSecMin();
    }
}

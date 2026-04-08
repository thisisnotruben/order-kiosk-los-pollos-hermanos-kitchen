package rarlog.me.kitchen.kitchen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.EqualsAndHashCode;
import rarlog.me.kitchen.dto.WorkDto;
import rarlog.me.kitchen.queue.RabbitMqProducer;
import rarlog.me.kitchen.repository.FoodItemRepository;

@Component
@Data
@EqualsAndHashCode(callSuper = false)
public class Worker extends Thread {

    public enum Status {
        IDLE,
        WORKING,
    }

    private final FoodItemRepository foodItemRepository;
    private final RabbitMqProducer messageProducer;

    private Status status = Status.IDLE;
    private WorkDto work;

    private static final Logger LOGGER = LoggerFactory.getLogger(Worker.class);

    public void prepareFoodItem(WorkDto work) {
        this.work = work;
        status = Status.WORKING;
        run();
    }

    @Override
    public void run() {
        try {
            int preperationTime = foodItemRepository.findById(work.getFoodId()).get().getPreperationTime();
            LOGGER.info(String.format("Worker will prepare food for time amount (sec): %s", preperationTime));

            sleep(preperationTime * 1000);
            LOGGER.info("Worker finished making food");

            status = Status.IDLE;
            messageProducer.sendWorkerMessage(work);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

package rarlog.me.kitchen.worker;

import lombok.Data;
import lombok.EqualsAndHashCode;
import rarlog.me.kitchen.dto.Work;
import rarlog.me.kitchen.kitchen.Kitchen;
import rarlog.me.kitchen.repository.FoodItemRepository;

@Data
@EqualsAndHashCode(callSuper = false)
public class Worker extends Thread {

    public enum Status {
        IDLE,
        WORKING,
    }

    private FoodItemRepository foodItemRepository;
    private Kitchen kitchen;

    private Status status = Status.IDLE;
    private Work work;

    public void prepareFoodItem(Work work) {
        this.work = work;
        start();
    }

    @Override
    public void run() {
        try {
            sleep(foodItemRepository.getReferenceById(work.getFoodId()).getPreperationTime());
            kitchen.workerFinished(this, work);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

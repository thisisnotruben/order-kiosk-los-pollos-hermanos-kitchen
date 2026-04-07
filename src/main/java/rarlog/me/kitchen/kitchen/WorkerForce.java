package rarlog.me.kitchen.kitchen;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import rarlog.me.kitchen.worker.Worker;

@Component
public class WorkerForce {

    @Value("${workforce.amount}")
    private int amountOfWorkers;

    private final List<Worker> workers = new ArrayList<>();

    public WorkerForce() {
        for (int i = 0; i < amountOfWorkers; i++) {
            workers.add(new Worker());
        }
    }

    public Worker getWorker() {
        for (Worker worker : workers) {
            if (worker.getStatus() == Worker.Status.IDLE) {
                return worker;
            }
        }
        return null;
    }

}

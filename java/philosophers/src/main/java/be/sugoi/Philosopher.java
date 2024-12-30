package be.sugoi;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.IntStream;

public class Philosopher implements Runnable {

    private static final int HOW_MANY = 6;
    private static final int MAX_WAIT_TIME = 3000;
    private final Random random = new Random();
    private final int id;
    private final Semaphore left;
    private final Semaphore right;

    public Philosopher(int id, Semaphore left, Semaphore right) {
        this.id = id;
        this.left = left;
        this.right = right;
    }

    @Override
    public void run() {
        while (true) {
            try {
                log("Thinking");
                Thread.sleep(random.nextInt(MAX_WAIT_TIME));
                log("Hungry");
                left.acquire();
                right.acquire();
                log("Eating");
                Thread.sleep(random.nextInt(MAX_WAIT_TIME));
            } catch (InterruptedException e) {
                break;
            }  finally {
                right.release();
                left.release();
            }
        }
    }

    private void log(String msg) {
        System.out.println("#" + id + " " + msg);
    }

    public static void main(String[] args) {
        var forks = IntStream.range(0, HOW_MANY)
                .mapToObj(i -> new Semaphore(1))
                .toList();
        var executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(HOW_MANY);
        for (int i = 1; i <= HOW_MANY; i++) {
            var left = i - 1;
            var right = i % HOW_MANY;
            if (i == HOW_MANY) {
                // Invert forks for last philosopher to avoid circular deadlock
                var temp = left;
                left = right;
                right = temp;
            }
            executor.execute(new Philosopher(i, forks.get(left), forks.get(right)));
        }
        executor.shutdown();
    }
}
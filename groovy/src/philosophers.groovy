#!/usr/bin/env groovy

import static groovyx.gpars.GParsPool.withPool

import groovy.transform.*
import java.util.concurrent.*

@TypeChecked
class Philosopher {
    int MAX_TIME = 1 * 1000
    int id
    Semaphore left, right

    private void randomPause() {
        sleep(ThreadLocalRandom.current().nextLong(MAX_TIME))
    }

    private void dine() {
        while (true) {
            say "thinking"
            randomPause()

            say "hungry"
            left.acquire()
            right.acquire()

            say "eating"
            randomPause()
            right.release()
            left.release()
        }
    }

    private void say(String msg) {
        printf("#%d %s\n", id, msg)
    }
}

int PHILOSOPHER_COUNT = 5

List<Semaphore> forks = (1..PHILOSOPHER_COUNT).collect { new Semaphore(1) }

List<Philosopher> philosophers = (1..PHILOSOPHER_COUNT).collect { i ->
    new Philosopher(id: i, left: forks[i - 1], right: forks[i % PHILOSOPHER_COUNT])
}

withPool(PHILOSOPHER_COUNT) {
    philosophers.eachParallel { it.dine() }
}
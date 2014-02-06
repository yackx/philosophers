#!/usr/bin/env groovy

import static groovyx.gpars.GParsPool.withPool

import groovy.transform.*
import java.util.concurrent.*

@TypeChecked
class Philosopher {

    enum State { THINKING, HUNGRY, EATING }

    int MAX_TIME = 1 * 1000

    int id
    State state
    Semaphore left, right

    private void randomPause() {
        sleep(ThreadLocalRandom.current().nextLong(MAX_TIME))
    }

    private void dine() {
        state = State.THINKING
        println this
        randomPause()

        state = State.HUNGRY
        println this
        left.acquire()
        right.acquire()

        state = State.EATING
        randomPause()
        println this
        right.release()
        left.release()

        dine()
    }

    @Override String toString() {
        "#${id} is $state"
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
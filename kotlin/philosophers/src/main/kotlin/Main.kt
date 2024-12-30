package be.sugoi

import java.util.Random
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import java.util.concurrent.ThreadPoolExecutor
import java.util.stream.IntStream

class Philosopher(
    private val id: Int,
    private val left: Semaphore,
    private val right: Semaphore
) : Runnable {

    private val random = Random()

    override fun run() {
        while (true) {
            try {
                log("Thinking")
                Thread.sleep(random.nextInt(MAX_WAIT_TIME).toLong())
                log("Hungry")
                left.acquire()
                right.acquire()
                log("Eating")
                Thread.sleep(random.nextInt(MAX_WAIT_TIME).toLong())
            } catch (e: InterruptedException) {
                break
            } finally {
                right.release()
                left.release()
            }
        }
    }

    private fun log(msg: String) = println("#$id $msg")

    companion object {
        private const val HOW_MANY = 6
        private const val MAX_WAIT_TIME = 3000

        @JvmStatic
        fun main(args: Array<String>) {
            val forks = IntStream.range(0, HOW_MANY)
                .mapToObj { Semaphore(1) }
                .toList()
            val executor = Executors.newFixedThreadPool(HOW_MANY) as ThreadPoolExecutor

            for (i in 1..HOW_MANY) {
                val (leftIndex, rightIndex) =
                    if (i != HOW_MANY) Pair(i - 1, i % HOW_MANY)
                    else Pair(0, i - 1) // Invert for last philosopher to avoid deadlock
                executor.execute(Philosopher(i, forks[leftIndex], forks[rightIndex]))
            }
            executor.shutdown()
        }
    }
}

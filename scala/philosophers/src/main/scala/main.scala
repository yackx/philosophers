import java.util.concurrent.{Executors, Semaphore, ThreadPoolExecutor}
import scala.util.Random

class Philosopher(id: Int, left: Semaphore, right: Semaphore) extends Runnable {
  private val MAX_WAIT_TIME = 3000
  private val random = new Random()

  override def run(): Unit = {
    while (true) {
      try {
        log("Thinking")
        Thread.sleep(random.nextInt(MAX_WAIT_TIME))
        log("Hungry")
        left.acquire()
        right.acquire()
        log("Eating")
        Thread.sleep(random.nextInt(MAX_WAIT_TIME))
      } catch {
        case e: InterruptedException => Thread.currentThread().interrupt(); return
      } finally {
        right.release()
        left.release()
      }
    }
  }
  
  private def log(msg: String): Unit = {
    println(s"#$id $msg")
  }
}

@main
def main(): Unit = {
  val HOW_MANY = 6
  val forks = (0 until HOW_MANY).map(_ => new Semaphore(1)).toList
  val executor = Executors.newFixedThreadPool(HOW_MANY).asInstanceOf[ThreadPoolExecutor]

  (1 to HOW_MANY).foreach { i =>
    val (left, right) = if (i == HOW_MANY) {
      (i % HOW_MANY, i - 1)
    } else {
      (i - 1, i % HOW_MANY)
    }
    executor.execute(new Philosopher(i, forks(left), forks(right)))
  }

  executor.shutdown()
}
package whg.common.threadpool

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext

class ThreadPools(globalEc: ExecutionContext) {

  implicit val global: ExecutionContext  = globalEc
  implicit val jdbc: ExecutorService     = Executors.newCachedThreadPool()
  implicit val jdbcEc: ExecutionContext  = ExecutionContext.fromExecutor(jdbc)
  implicit val otherIO: ExecutorService  = Executors.newFixedThreadPool(32)
  implicit val otherIOEc: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(32))

}

package whg.common.threadpool

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext

class ThreadPools(globalEc: ExecutionContext) {

  implicit val global: ExecutionContext  = globalEc
  implicit val jdbc: ExecutionContext    = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
  implicit val otherIO: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(32))

}

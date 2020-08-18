package whg.common.database

import java.util.concurrent.Executors

import io.getquill.SnakeCase
import cats.effect.{Blocker, ContextShift, IO, Resource}
import doobie.quill.DoobieContext
import doobie.util.transactor.Transactor
import io.getquill.PostgresJdbcContext
import whg.common.threadpool.ThreadPools

import scala.concurrent.ExecutionContext

class DatabaseDriver(threadPools: ThreadPools)(implicit cs: ContextShift[IO]) {

  private val quillCtx = new PostgresJdbcContext(SnakeCase, "db.ctx")

  private val blocker: Blocker    = Blocker.liftExecutorService(threadPools.jdbc)
  private val connectionAwaitPool = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(32))

  val xa: Transactor[IO]= Transactor.fromDataSource[IO](quillCtx.dataSource, connectionAwaitPool, blocker)

  val ctx = new DoobieContext.Postgres[SnakeCase](SnakeCase)
}

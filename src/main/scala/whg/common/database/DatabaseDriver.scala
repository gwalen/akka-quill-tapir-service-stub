package whg.common.database

import java.util.concurrent.Executors

import io.getquill.PostgresAsyncContext
import io.getquill.SnakeCase
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import cats.effect.{Blocker, ContextShift, IO, Resource}
import doobie.quill.DoobieContext
import doobie.util.transactor.Transactor
import io.getquill.PostgresJdbcContext
import whg.common.threadpool.ThreadPools
import whg.main.config.DatabaseConfig

import scala.concurrent.ExecutionContext

class DatabaseDriver(dbConfig: DatabaseConfig, threadPools: ThreadPools)(implicit cs: ContextShift[IO]) {

  //HikariCP is used by default
//  val quillCtx = new PostgresAsyncContext(SnakeCase, "db.ctx")
  val ctx = new PostgresJdbcContext(SnakeCase, "db.ctx")

  private val blocker: Blocker    = Blocker.liftExecutorService(threadPools.jdbc)
  private val connectionAwaitPool = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(32))

  val xa: Transactor[IO]= Transactor.fromDataSource[IO](ctx.dataSource, connectionAwaitPool, blocker)


//  def transactor(): Resource[IO, HikariTransactor[IO]] = {
//    for {
//      ec <- ExecutionContexts.fixedThreadPool[IO](32)
//      be <- Blocker[IO]
//      xa <- HikariTransactor.newHikariTransactor[IO](dbConfig.driver, dbConfig.url, dbConfig.user, dbConfig.password, ec, be)
//    } yield (xa)
//  }

  val doobieCtx = new DoobieContext.Postgres[SnakeCase](SnakeCase)
}

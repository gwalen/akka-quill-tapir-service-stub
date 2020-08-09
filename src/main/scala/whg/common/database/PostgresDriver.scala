package whg.common.database

import io.getquill.PostgresAsyncContext
import io.getquill.SnakeCase

class PostgresDriver() {

  //HikariCP is used by default
  val ctx = new PostgresAsyncContext(SnakeCase, "db.ctx")
}

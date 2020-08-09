package whg.main.dependencies

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import com.typesafe.config.{Config, ConfigFactory}
import whg.main.config._

import scala.concurrent.ExecutionContext

trait CommonLayer { self =>

  implicit val system: ActorSystem
  implicit def executor: ExecutionContext

  lazy val config: Config                  = ConfigFactory.load()
  implicit lazy val logger: LoggingAdapter = system.log

  lazy val dbConfig = DatabaseConfig(
    config.getString("db.flyway.dburl"),
    config.getString("db.ctx.user"),
    config.getString("db.ctx.password"),
    config.getBoolean("db.flyway.migration-during-boot"))

  lazy val serverConfig: ServerConfig = ServerConfig(
    config.getString("http.interface"),
    config.getInt("http.port"),
    config.getString("http.hostname")
  )

}

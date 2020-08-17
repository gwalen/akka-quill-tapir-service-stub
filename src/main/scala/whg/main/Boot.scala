package whg.main

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cats.effect.ContextShift
import cats.effect.IO
import org.flywaydb.core.Flyway
import whg.main.dependencies._

import scala.concurrent.ExecutionContext

trait Setup
  extends HttpComponents
    with BusinessLogicComponents
    with DatabaseComponents
    with CommonLayer {

  lazy val docsRoutes: Route = openApiDocsGenerator.docRoutes(countryRouter.endpoints ++ reservationRouter.endpoints)

  lazy val apiRoutes: Route =
    healthRouter.routes ~
    countryRouter.routes ~
    reservationRouter.routes ~
    docsRoutes

}

object Boot extends App with Setup {

  override implicit val system: ActorSystem        = ActorSystem("whg", config)
  override implicit val executor: ExecutionContext = system.dispatcher
  // used for automatic shifting back execution to global thread pool (stored in executor)
  override implicit val cs: ContextShift[IO]       = IO.contextShift(executor)

  // Apply database migration
  if (dbConfig.flywayMigrationDuringBoot) {
    val flyway = Flyway.configure().dataSource(dbConfig.url, dbConfig.user, dbConfig.password).load()
    flyway.migrate()
  }

  Http().bindAndHandle(apiRoutes, serverConfig.interface, serverConfig.port)
}

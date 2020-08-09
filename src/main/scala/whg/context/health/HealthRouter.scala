package whg.context.health

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route

class HealthRouter {

  val routes: Route = {
    pathPrefix("health") {
      (get & path("check") & pathEndOrSingleSlash) {
        complete {
          OK -> "OK"
        }
      }
    }
  }
}

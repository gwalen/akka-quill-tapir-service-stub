package whg.main.dependencies

import com.softwaremill.macwire.wire
import whg.common.restapi.OpenApiDocsGenerator
import whg.context.country.restapi.CountryRouter
import whg.context.health.HealthRouter
import whg.context.reservation.restapi.ReservationRouter

trait HttpComponents { self: CommonLayer with BusinessLogicComponents =>

  lazy val healthRouter: HealthRouter                 = wire[HealthRouter]
  lazy val countryRouter: CountryRouter               = wire[CountryRouter]
  lazy val reservationRouter: ReservationRouter       = wire[ReservationRouter]
  lazy val openApiDocsGenerator: OpenApiDocsGenerator = wire[OpenApiDocsGenerator]
}

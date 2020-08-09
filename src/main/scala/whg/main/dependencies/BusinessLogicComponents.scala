package whg.main.dependencies

import com.softwaremill.macwire.wire
import whg.context.country.application.CountryService
import whg.context.reservation.application.ReservationService

trait BusinessLogicComponents { self: CommonLayer with DatabaseComponents =>

  lazy val countryService: CountryService = wire[CountryService]
  lazy val reservationService: ReservationService = wire[ReservationService]
}

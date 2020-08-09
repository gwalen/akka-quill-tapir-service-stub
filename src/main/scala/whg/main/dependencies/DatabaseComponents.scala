package whg.main.dependencies

import com.softwaremill.macwire.wire
import whg.common.database.PostgresDriver
import whg.context.country.persistance.CountryRepository
import whg.context.reservation.persistance.ReservationRepository

trait DatabaseComponents { self: CommonLayer =>

  lazy val postgresDriver: PostgresDriver = new PostgresDriver()

  lazy val countryRepository: CountryRepository              = wire[CountryRepository]
  lazy val reservationRepository: ReservationRepository = wire[ReservationRepository]
}

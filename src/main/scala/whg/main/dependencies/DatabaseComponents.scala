package whg.main.dependencies

import com.softwaremill.macwire.wire
import whg.common.database.DatabaseDriver
import whg.context.country.persistance.CountryRepository
import whg.context.reservation.persistance.ReservationRepository

trait DatabaseComponents { self: CommonLayer =>

  lazy val postgresDriver: DatabaseDriver = wire[DatabaseDriver]

  lazy val countryRepository: CountryRepository         = wire[CountryRepository]
  lazy val reservationRepository: ReservationRepository = wire[ReservationRepository]
}

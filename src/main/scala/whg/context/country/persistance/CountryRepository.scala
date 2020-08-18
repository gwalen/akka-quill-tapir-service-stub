package whg.context.country.persistance

import akka.Done
import cats.effect.IO
import whg.common.database.DatabaseDriver
import whg.context.country.domain.CountryCurrency
import whg.context.country.domain.CountryTelephonePrefix

import cats.implicits._
import doobie.ConnectionIO
import doobie.implicits._

class CountryRepository(postgresDriver: DatabaseDriver) {
  import postgresDriver._
  import ctx._

  def insertCurrency(countryCurrency: CountryCurrency): IO[Done] = {
    val q = quote {
      query[CountryCurrency].insert(lift(countryCurrency))
    }
    ctx.run(q).transact(xa).map(_ => Done)
  }

  def deleteCurrency(country: String): IO[Done] = {
    val q = quote {
      query[CountryCurrency].filter(cc => cc.country == lift(country)).delete
    }
    ctx.run(q).transact(xa).map(_ => Done)
  }

  def findCurrency(country: String): IO[Option[CountryCurrency]] = {
    val q = quote {
      query[CountryCurrency].filter(cc => cc.country == lift(country))
    }
    ctx.run(q).transact(xa).map(result => result.headOption)
  }

  def findAllCurrencies(): IO[List[CountryCurrency]] =
    ctx.run(query[CountryCurrency]).transact(xa)

  def insertTelephonePrefix(countryTelephonePrefix: CountryTelephonePrefix): IO[Done] = {
    //Quoting is implicit when writing a query in a run statement.
    ctx.run(query[CountryTelephonePrefix].insert(lift(countryTelephonePrefix))).transact(xa).map(_ => Done)
  }

  def deleteTelephonePrefix(country: String): IO[Done] = {
    ctx.run(query[CountryTelephonePrefix].filter(cc => cc.country == lift(country)).delete).transact(xa).map(_ => Done)
  }

  def findTelephonePrefix(country: String): IO[Option[CountryTelephonePrefix]] = {
    ctx.run(query[CountryTelephonePrefix].filter(cc => cc.country == lift(country)))
      .transact(xa)
      .map(result => result.headOption)
  }

  def findAllTelephonePrefixes(): IO[List[CountryTelephonePrefix]] =
    ctx.run(query[CountryTelephonePrefix]).transact(xa)
}



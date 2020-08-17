package whg.context.country.persistance

import akka.Done
import cats.effect.IO
import whg.common.database.DatabaseDriver
import whg.common.threadpool.ThreadPools
import whg.context.country.domain.CountryCurrency
import whg.context.country.domain.CountryTelephonePrefix

import cats.implicits._
import doobie.ConnectionIO
import doobie.implicits._

// TODO: remove thread pool add
class CountryRepository(postgresDriver: DatabaseDriver, threadPools: ThreadPools) {
  import postgresDriver._
  import doobieCtx._

  def insertCurrency(countryCurrency: CountryCurrency): IO[Done] = {
    val q = quote {
      query[CountryCurrency].insert(lift(countryCurrency))
    }
    doobieCtx.run(q).transact(xa).map(_ => Done)
  }

  def insertCurrency2(countryCurrency: CountryCurrency): IO[Long] = {
    val q = quote {
      query[CountryCurrency].insert(lift(countryCurrency))
    }
    doobieCtx.run(q).transact(xa)
  }

  def deleteCurrency(country: String): IO[Done] = {
    val q = quote {
      query[CountryCurrency].filter(cc => cc.country == lift(country)).delete
    }
    doobieCtx.run(q).transact(xa).map(_ => Done)
  }

  def findCurrency(country: String): IO[Option[CountryCurrency]] = {
    val q = quote {
      query[CountryCurrency].filter(cc => cc.country == lift(country))
    }
    doobieCtx.run(q).transact(xa).map(result => result.headOption)
  }

  def findAllCurrencies(): IO[List[CountryCurrency]] =
    doobieCtx.run(query[CountryCurrency]).transact(xa)

  def insertTelephonePrefix(countryTelephonePrefix: CountryTelephonePrefix): IO[Done] = {
    //Quoting is implicit when writing a query in a run statement.
    doobieCtx.run(query[CountryTelephonePrefix].insert(lift(countryTelephonePrefix))).transact(xa).map(_ => Done)
  }

  def deleteTelephonePrefix(country: String): IO[Done] = {
    doobieCtx.run(query[CountryTelephonePrefix].filter(cc => cc.country == lift(country)).delete).transact(xa).map(_ => Done)
  }

  def findTelephonePrefix(country: String): IO[Option[CountryTelephonePrefix]] = {
    doobieCtx.run(query[CountryTelephonePrefix].filter(cc => cc.country == lift(country)))
      .transact(xa)
      .map(result => result.headOption)
  }

  def findAllTelephonePrefixes(): IO[List[CountryTelephonePrefix]] =
    doobieCtx.run(query[CountryTelephonePrefix]).transact(xa)
}



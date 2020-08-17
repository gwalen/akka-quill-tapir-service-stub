package whg.context.country.persistance

import akka.Done
import whg.common.database.DatabaseDriver
import whg.common.threadpool.ThreadPools
import whg.context.country.domain.CountryCurrency
import whg.context.country.domain.CountryTelephonePrefix

import scala.concurrent.Future

class CountryRepository(postgresDriver: DatabaseDriver, threadPools: ThreadPools) {
  import postgresDriver.ctx
  import ctx._
  import threadPools.jdbcEc

  def insertCurrency(countryCurrency: CountryCurrency): Future[Done] = {
    val q = quote {
      query[CountryCurrency].insert(lift(countryCurrency))
    }
    ctx.run(q).map(_ => Done)
  }

  def insertCurrency2(countryCurrency: CountryCurrency): Future[Long] = {
    val q = quote {
      query[CountryCurrency].insert(lift(countryCurrency))
    }
    ctx.run(q)
  }

  def deleteCurrency(country: String): Future[Done] = {
    val q = quote {
      query[CountryCurrency].filter(cc => cc.country == lift(country)).delete
    }
    ctx.run(q).map(_ => Done)
  }

  def findCurrency(country: String): Future[Option[CountryCurrency]] = {
    val q = quote {
      query[CountryCurrency].filter(cc => cc.country == lift(country))
    }
    ctx.run(q).map(result => result.headOption)
  }

  def findAllCurrencies(): Future[List[CountryCurrency]] =
    ctx.run(query[CountryCurrency])

  def insertTelephonePrefix(countryTelephonePrefix: CountryTelephonePrefix): Future[Done] = {
    //Quoting is implicit when writing a query in a run statement.
    ctx.run(query[CountryTelephonePrefix].insert(lift(countryTelephonePrefix))).map(_ => Done)
  }

  def deleteTelephonePrefix(country: String): Future[Done] = {
    ctx.run(query[CountryTelephonePrefix].filter(cc => cc.country == lift(country)).delete).map(_ => Done)
  }

  def findTelephonePrefix(country: String): Future[Option[CountryTelephonePrefix]] = {
    ctx.run(query[CountryTelephonePrefix].filter(cc => cc.country == lift(country)))
      .map(result => result.headOption)
  }

  def findAllTelephonePrefixes(): Future[List[CountryTelephonePrefix]] =
    ctx.run(query[CountryTelephonePrefix])
}



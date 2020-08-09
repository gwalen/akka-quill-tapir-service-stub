package whg.context.country.persistance

import akka.Done
import whg.common.database.PostgresDriver
import whg.context.country.domain.CountryCurrency
import whg.context.country.domain.CountryTelephonePrefix

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

//TODO: use Cats IO for async execution

class CountryRepository(postgresDriver: PostgresDriver)(implicit ec: ExecutionContext) {
  import postgresDriver._
  import ctx._

  def insertCurrency(countryCurrency: CountryCurrency): Future[Done] = {
    val q = quote {
      query[CountryCurrency].insert(lift(countryCurrency))
    }
    ctx.run(q).map(_ => Done)
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
}



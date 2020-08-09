import io.github.davidmweber.FlywayPlugin
import io.github.davidmweber.FlywayPlugin.autoImport._
import sbt.Keys._
import sbt._

object PostgresMigrations {
  private val env = sys.env

  lazy val dbName     = env.getOrElse("POSTGRES_DATABASE", "data_base_name")
  lazy val testDbName = env.getOrElse("POSTGRES_TEST_DATABASE", s"$dbName-test")
  lazy val host       = env.getOrElse("POSTGRES_HOST", "localhost")
  lazy val port       = env.getOrElse("POSTGRES_PORT", "5432")
  lazy val dbUser     = env.getOrElse("POSTGRES_USER", "postgres")
  lazy val dbPassword = env.getOrElse("POSTGRES_PASSWORD", "postgres")
  lazy val dbUrl      = s"jdbc:postgresql://$host:$port/$dbName"
  lazy val dbUrlForIt = s"jdbc:postgresql://$host:$port/$testDbName"

  private lazy val commonSettings = Seq(
    flywayUser := dbUser,
    flywayPassword := dbPassword,
    flywayLocations := Seq(s"filesystem:${(resourceDirectory in Compile).value.getPath}/db/migration"),
    flywayOutOfOrder := true
  )

  lazy val settings: Seq[Setting[_]] = commonSettings ++ Seq(
    flywayUrl := dbUrl
  )

  lazy val itSettings: Seq[Setting[_]] =
    FlywayPlugin.flywayBaseSettings(IntegrationTest) ++ commonSettings ++ Seq(
      flywayUrl := dbUrlForIt
    )

}

package whg.main.config

case class DatabaseConfig(
//  driver: String,
  url: String,
  user: String,
  password: String,
  flywayMigrationDuringBoot: Boolean
)

package whg.main.config

case class DatabaseConfig(url: String, user: String, password: String, flywayMigrationDuringBoot: Boolean)

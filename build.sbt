
name         := "lookup-service"
organization := "whg"
version      := "1.0.0"
scalaVersion := "2.13.2"

resolvers ++= Seq(
  Resolver.typesafeRepo("releases")
)

libraryDependencies ++= Dependencies.allDependencies

// Migrations
enablePlugins(FlywayPlugin)
PostgresMigrations.settings

// SBT Native packager
enablePlugins(JavaAppPackaging, DockerPlugin)

dockerExposedPorts ++= Seq(8080)
dockerBaseImage := "adoptopenjdk/openjdk11:slim"
dockerUpdateLatest := true

// to start app in separate JVM and allow killing it without restarting SBT (developing purpose)
run / fork := true
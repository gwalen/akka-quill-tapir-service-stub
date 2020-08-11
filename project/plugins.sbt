logLevel := Level.Warn

resolvers += "Flyway" at "https://davidmweber.github.io/flyway-sbt.repo"
resolvers += Resolver.bintrayRepo("colisweb", "sbt-plugins")

addSbtPlugin("io.github.davidmweber" % "flyway-sbt"          % "6.4.2")
addSbtPlugin("com.typesafe.sbt"      % "sbt-native-packager" % "1.7.3")
addSbtPlugin("au.com.onegeek"        %% "sbt-dotenv"         % "2.1.146")
addSbtPlugin("com.colisweb.sbt"      % "sbt-datadog"         % "1.2.0")

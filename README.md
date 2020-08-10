# Lookup service stub

### Tools used :

* Akk-http (rest-api)
* Tapir (open-api docs)
* Quill (db)
* Flyway (db migration)
* Circe (json marshalling)
* Logback (logging)
* MacWire (DI framework)
* ScalaTest and ScalaMock (testing) 
* Sbt (run app, create docker image)
* Cats (fp utils)

### Create schema for application

create schema :

`docker exec -it -u postgres postgis96 sh -c "psql -c 'create database lookupservice'"`


### API docs 

Yaml rest-api docs are exposed on `http://localhost:8080/docs/docs.yaml`

To read it using swagger-ui, go to `http://localhost:8080/docs/index.html#/` and in the top input type local yaml api address 
(`http://localhost:8080/api/docs/docs.yaml`)
Run
===

Open container:

```
docker-compose run dev
cd structurizr-app
```

Run code:

```
mvn clean compile exec:java
```

or

```
mvn clean dependency:copy-dependencies package && java -cp target/structurizr-app-1.0-SNAPSHOT.jar:target/dependency/* com.ab.dna.App
```

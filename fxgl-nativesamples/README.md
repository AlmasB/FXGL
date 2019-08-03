## Developing native samples

1. Install FXGL locally so that `fxgl-nativesamples` has access to it.

```
mvn clean -DskipTests=true -Dgpg.skip=true install
```

2. `cd` to `fxgl-nativesamples` module and run

```
mvn clean client:compile
mvn client:link
mvn client:run
```
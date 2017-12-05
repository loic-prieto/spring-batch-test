# Spring Batch test

This is a repository to hold basic tests for Spring batch.

As it is, it is only the tutorial implementation of the Spring Batch documentation, but I want to expand upon it
to serve as the basis of a talk. Implemented with Spring boot.

## Instructions

To generate the executable jar, just execute the package goal with maven:

```bash
mvn clean package
```

To run a test execution of the batch process just execute with java -jar
```bash
java -jar target/spring-batch-*.jar
```

If there are some errors, I recommend running with the debug flag:

```bash
java -jar target/spring-batch-*.jar --debug
```

At least Maven 3.2 and Java 1.8 is needed.
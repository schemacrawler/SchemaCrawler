@echo off
java -Djava.util.logging.config.class=sf.util.LoggingConfig -cp "%~dp0/lib/*";. schemacrawler.testdb.TestSchemaCreatorMain %*

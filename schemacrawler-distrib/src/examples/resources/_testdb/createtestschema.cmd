@echo off
java -cp "%~dp0/lib/*";. schemacrawler.testdb.TestSchemaCreatorMain %*

@echo off
color 1F
title SchemaCrawler Test Database
java -Djava.util.logging.config.class=sf.util.LoggingConfig -cp "%~dp0/lib/*";. schemacrawler.testdb.TestDatabase

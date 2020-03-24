@echo off
color 1F
title SchemaCrawler Test Database
java -cp "%~dp0/lib/*";. schemacrawler.testdb.TestDatabase

@echo off
java -classpath ../../_schemacrawler/lib/*;lib/* schemacrawler.tools.integration.spring.Main -context-file=context.xml -executable=executableForSchema -datasource=datasource
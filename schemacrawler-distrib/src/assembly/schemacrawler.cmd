@java -Djava.util.logging.config.class=sf.util.LoggingConfig "-Djava.library.path=%~dp0/lib/" -classpath "%~dp0/lib/*";"%~dp0/config";. schemacrawler.Main %*

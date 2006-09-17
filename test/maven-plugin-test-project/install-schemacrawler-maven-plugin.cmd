mvn install:install-file -Dfile=../../_distrib/schemacrawler-3.8.jar -DgroupId=schemacrawler -DartifactId=schemacrawler-maven-plugin -Dversion=3.8 -Dpackaging=maven-plugin -DgeneratePom=true

mvn help:describe -DgroupId=schemacrawler -DartifactId=schemacrawler-maven-plugin

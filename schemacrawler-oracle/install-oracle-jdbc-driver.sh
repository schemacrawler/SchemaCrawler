mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=$2 -Dfile=$1 -Dpackaging=jar -DgeneratePom=true

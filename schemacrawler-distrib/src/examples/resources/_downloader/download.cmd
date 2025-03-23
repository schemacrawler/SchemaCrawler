@echo off
echo Downloading %1
java -jar ivy-2.5.3.jar -ivy %1_ivy.xml -settings ivysettings.xml -retrieve "../_schemacrawler/lib/[artifact]-[revision](-[classifier]).[ext]"

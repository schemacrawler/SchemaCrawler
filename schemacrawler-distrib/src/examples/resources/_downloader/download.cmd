@echo off
echo Downloading %1
java -jar ivy-2.5.1.jar -ivy %1_ivy.xml -settings ivysettings.xml -retrieve "../_schemacrawler/lib/[artifact]-[revision]-[type].[ext]"

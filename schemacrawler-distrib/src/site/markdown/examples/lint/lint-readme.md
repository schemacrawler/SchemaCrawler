# SchemaCrawler - Lint Example

## Description
SchemaCrawler looks for potential database design issues. Follow the steps
below to run SchemaCrawler Lint.

## How to Run
1. Follow the instructions in the [commandline](../commandline/commandline-readme.html) example 
2. To find schema design issues, run 
   `sc.cmd -server=hsqldb -database=schemacrawler -password= -infolevel=standard -command=lint` 
   (use `sc.sh` instead of `sc.cmd` on Unix)

## How to Experiment
1. Create the SchemaCrawler Lint report in HTML5 and JSON formats. 
2. Create a customized SchemaCrawler Lint report using a linter configuration file. 
  1. Start a command shell in the `lint` example directory.
  2. To find schema design issues, run 
  `java -Dschemacrawer.linter_configs.file=schemacrawler-linter-configs.xml 
  -cp .;..\_schemacrawler\lib\*.jar 
  schemacrawler.tools.hsqldb.Main -database=schemacrawler
  -user=sa -password= -infolevel=standard -command=lint`  
  (use `sc.sh` instead of `sc.cmd` on Unix)
3. Try creating your own database lints in a new jar - see [SchemaCrawler Lint](http://schemacrawler.sourceforge.net/lint.html). 

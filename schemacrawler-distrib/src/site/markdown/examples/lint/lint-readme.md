# SchemaCrawler - Lint Example

## Description
SchemaCrawler looks for potential database design issues. Follow the steps
below to run SchemaCrawler Lint.

## How to Run
1. Follow the instructions in the [commandline](../commandline/commandline-readme.html) example 
2. To find schema design issues, run 
   `schemacrawler.cmd -server=hsqldb -database=schemacrawler -password= -infolevel=standard -command=lint` 
   (use `schemacrawler.sh` instead of `schemacrawler.cmd` on Unix)

## How to Experiment
1. See help on all of the available lints, run 
   `schemacrawler.cmd -command=lint -help` 
   (use `schemacrawler.sh` instead of `schemacrawler.cmd` on Unix)
1. Create the SchemaCrawler Lint report in HTML5 and JSON formats. 
2. Create a customized SchemaCrawler Lint report using a linter configuration file. 
  1. Copy the `schemacrawler-linter-configs.xml` file to the SchemaCrawler distribution directory, that is, the `_schemacrawler` directory.
  2. To find schema design issues, run with an additional command-line option,
  `-linterconfigs=schemacrawler-linter-configs.xml`
3. Try creating your own database lints in a new jar - see [SchemaCrawler Lint](http://www.schemacrawler.com/lint.html). 

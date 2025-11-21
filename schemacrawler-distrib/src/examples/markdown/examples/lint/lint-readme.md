# SchemaCrawler - Lint Example

## Description
SchemaCrawler looks for potential database design issues. Follow the steps
below to run SchemaCrawler Lint.

## How to Setup
1. Make sure that SchemaCrawler is [installed on your system](https://www.schemacrawler.com/downloads.html)
2. Make sure that `schemacrawler` is on your PATH
   
## How to Run
1. Follow the instructions in the [commandline](../commandline/commandline-readme.html) example 
2. To find schema design issues, run 
   `schemacrawler.cmd --server=hsqldb --database=schemacrawler --password= --info-level=standard --command=lint` 
   (use `schemacrawler.sh` instead of `schemacrawler.cmd` on Unix)

## How to Experiment
1. See help on all of the available lints, run 
   `schemacrawler.cmd lint --help` 
   (use `schemacrawler.sh` instead of `schemacrawler.cmd` on Unix)
2. Create the SchemaCrawler Lint report in HTML5 format. 
3. Create the SchemaCrawler Lint report in JSON or YAML format, using an additional `-F json` or `-F yaml`. 
4. Create a customized SchemaCrawler Lint report using a linter configuration file. 
   1. Copy the `schemacrawler-linter-configs.yaml` file to the SchemaCrawler distribution directory, that is, the `_schemacrawler` directory.
   2. To find schema design issues, run with an additional command-line option,
      `--linter-configs=schemacrawler-linter-configs.yaml`
5. Try creating your own database lints in a new jar - see [SchemaCrawler Lint](https://www.schemacrawler.com/lint.html). 
6. Generate lint reports in JSON format. First download Jackson using instructions in the `serialize` example.
   Then run, 
   `schemacrawler.cmd --server=hsqldb --database=schemacrawler --password= --info-level=standard --command=lint,serialize --output-format=json` 
   (use `schemacrawler.sh` instead of `schemacrawler.cmd` on Unix)

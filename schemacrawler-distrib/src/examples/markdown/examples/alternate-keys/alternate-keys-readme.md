# SchemaCrawler - Alternate Keys Example

## Description
SchemaCrawler allows you to provide information about alternate keys for a table 
in a YAML file with the `--attributes-file` command-line switch.

## How to Setup
1. Make sure that java is on your PATH
2. Start a command shell in the `_downloader` directory 
3. Run `download.cmd jackson` (or `download.sh jackson` on Unix) to
   install serialization support using Jackson

## How to Run
1. Make sure that java is on your PATH
2. Start the test database server by following instructions in the `_testdb/README.html` file
3. Start a command shell in the `alternate-keys` example directory 
4. Run `alternate-keys.cmd alternate-keys.yaml alternate-keys.png` (or `alternate-keys.sh alternate-keys.yaml alternate-keys.png` on Unix). 
5. View the image in `alternate-keys.png` to see the alternate keys that were loaded from the YAML file

## How to Experiment
- Modify `alternate-keys.yaml` and rerun the command

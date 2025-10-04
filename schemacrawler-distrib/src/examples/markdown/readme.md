# SchemaCrawler

## Introduction

Please review the [SchemaCrawler website](https://www.schemacrawler.com/) for FAQs, how-tos, and so on. 
This download contains the main SchemaCrawler distribution, as well as examples. You can extend the 
SchemaCrawler distribution by downloading additional libraries that SchemaCrawler integrates with. You can also 
download additional JDBC drivers, and have SchemaCrawler pick them up automatically. Once you are happy 
with the customization of your installation, you can copy the `_schemacrawler` folder to any location 
on your system, and use it from there.

## Customizing Your SchemaCrawler Installation

You can customize your SchemaCrawler installation, to add functionality. This is an optional step.

### Database Drivers

Download database drivers for your database, and put the jar files into the `lib` folder. SchemaCrawler 
will pick them up automatically.

### Other Libraries

Follow these instructions to download and install libraries that SchemaCrawler integrates with, using 
the provided scripts.

1. Make sure that java is on your PATH
2. Start a command shell in the `_downloader` directory 
3. Run `download.cmd` (or `download.sh` on Unix). 
   Provide one of the following arguments:    
    - `plugins` - for additional SchemaCrawler plug-ins  
    - `javascript` - for JavaScript language scripting with SchemaCrawler, for Java 15 and upwards
    - `python` - for Python language scripting with SchemaCrawler
    - `velocity` - for Apache Velocity templating with SchemaCrawler

## Examples

This folder contains a number of examples, as well as a database server that has an example schema and 
data. Each folder illustrates a SchemaCrawler capability. Read the readme file in each example folder 
for instructions.

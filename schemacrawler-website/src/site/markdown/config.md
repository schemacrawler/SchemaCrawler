# SchemaCrawler Configuration

SchemaCrawler is highly configurable. You can configure exactly what database schema metadata you want to retrieve, since that can affect the time taken to crawl the database. You can configure what the output of SchemaCrawler commands looks like. SchemaCrawler uses the [lightbend/config](https://github.com/lightbend/config) configuration library for configuration, so you can refer to the documentation for property overrides.


## Configuration

SchemaCrawler provides a number of command-line options for commonly used configuration, for example `--no-info` to hide database information in the output. These options and many more can be set in the SchemaCrawler configuration file too. Click on the link for an example of the complete [schemacrawler.config.properties](config/schemacrawler.config.properties) file. Options specified on the command-line always take precendence over those in configuration files.

The order of loading configuration settings is:
1. From the in-built default values
2. Which can be overridden by settings in a CLASSPATH resource with a stem of `schemacrawler.config`, and which can be either a Java properties file (`.properties`), a JSON file (`.json`), or a human-friendly JSON superset (`.conf`)
3. Which can be overridden by settings in a similar configuration file with a stem of `schemacrawler.config`, and specified on the Java command-line either with a `-Dconfig.file=<path>` property, or a `--config-file=<path>` application argument
4. And individual properties can be overridden on the Java command-line with a `-D<property>=<value>`, for example, `-Dschemacrawler.format.hide_primarykey_names=true`
5. Which in turn can be overridden by other command-line options such as `--no-info`

For more information on how the the configuration is loaded, please refer to the [lightbend/config](https://github.com/lightbend/config) documentation. In the main SchemaCrawler distribution, complete configuration files are provided in the `config/` and put on the classpath.


## Color Map

SchemaCrawler allows you to override colors assigned to schemas in HTML output and in diagrams. Click on the link for an example of the [schemacrawler.colormap.properties](config/schemacrawler.colormap.properties) file. The configuration is loaded by default in a file with a stem of `schemacrawler.colormap`, following loading predence similar to what is described above. You can override color map properties with keys prefixed with `schemacrawler.format.color_map`.


## Configuration Via the API

All of the configuration options available via configuration files can be done programmatically using the SchemaCrawler API. See the examples in the [schemacrawler-examplecode](https://github.com/schemacrawler/SchemaCrawler/tree/main/schemacrawler-examplecode/src/main/java/com/example) project.


## Lint Configuration

See details on configuring lints on the [lints page](lint.html).


## Extending Metadata With Attributes

See details of how to extend SchemaCrawler metadata with table and column metadata and [weak associations](weak-associations.html) with [attributes](attributes.html).


## Data Dictionary Extensions

See details of how to extend SchemaCrawler with [Data Dictionary Extensions](data-dictionary-extensions.html).


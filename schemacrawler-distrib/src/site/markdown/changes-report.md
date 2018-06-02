# SchemaCrawler Change History

<a name="a14.21.03"></a>
## Release 14.21.03 - 2018-06-10

- Add tests for embedded MonetDB


<a name="a15.01.01"></a>
## Release 15.01.01 - 2018-05-21

- Add support for java.sql.Types REF_CURSOR, TIME_WITH_TIMEZONE, and TIMESTAMP_WITH_TIMEZONE
- Fixed issue with lint reporting "unique index with nullable columns" on computed columns

<a name="a14.21.01"></a>
## Release 14.21.01 - 2018-05-14

- Fixed issue #184 with with support for older Oracle versions in the SchemaCrawler Oracle plugin
- Changed API for SchemaCrawler plugins

<a name="a14.20.06"></a>
## Release 14.20.06 - 2018-05-02

- Fixed issue #181 with more control (and configuration) over graph generation

<a name="a14.20.05"></a>
## Release 14.20.05 - 2018-04-19

- Fixed issue #178 with JSON data generation

<a name="a14.20.04"></a>
## Release 14.20.04 - 2018-04-04

- Docker image updated with non-root user, and documentation updated for latest Docker version

<a name="a14.20.03"></a>
## Release 14.20.03 - 2018-03-10

- Website updates

<a name="a14.20.02"></a>
## Release 14.20.02 - 2018-03-07

* Upgrade to support Apache Velocity 2.0.
* Added remarks for tables and columns to Microsoft SQL Server test schema.

<a name="a14.20.01"></a>
## Release 14.20.01 - 2018-03-05

* Make -routines return no routines by default, to make it easier for first-time users.
* Support a pure Java implementation of Graphviz (nidi3/graphviz-java), for cases where Graphviz cannot be installed.
* Changed CommandProvider API

<a name="a14.19.01"></a>
## Release 14.19.01 - 2018-02-04

* Fixing bug when retrieving Oracle procedure metadata
* Adding test support for routines
* Adding test code for retrieving result set metadata

<a name="a14.18.01"></a>
## Release 14.18.01 - 2018-01-10

* Ignore MariaDb driver for MySQL if both are on the classpath.
* Verify support for unnamed primary keys and foreign keys.
* Support unnamed foreign keys.
* System to build reference schema for most common databases.
* Tests for unusual data types, user-defined data types, domains and temporary tables.
* Support for generating SQLite database from scripts.
* Added tests for ascending and descending columns in indexes.
* Getting MySQL plugin to support MariaDB as well
* Consistent treatment for hidden and generated table columns, with special marking in details output
* Support for Oracle 12c identity or auto-incremented columns

<a name="a14.17.05"></a>
## Release 14.17.05 - 2017-12-28

* Website updates, including link to the SchemaCrawler Web Application.

<a name="a14.17.04"></a>
## Release 14.17.04 - 2017-12-10

* Removing index unquoting work-around for old versions of the SQLite JDBC driver, and distributing with the latest driver.
* Making SQL type map a database specific property, which can be overridden by database plugins.
* Moving website to GitHub pages.

## Release 14.17.03 - 2017-11-21
<a name="a14.17.03"></a>

* Fixing a regression with SQLite databases created with double-quoted identifiers.

## Release 14.17.02 - 2017-11-20
<a name="a14.17.02"></a>

* Fix for broken API call in Maven archetype.
* Update to Apache Velocity example to quote database object names.

## Release 14.17.01 - 2017-11-19
<a name="a14.17.01"></a>

* Database objects getName() returns unquoted names, and getFullName() returns a name quoted using default quoting rules. Text output is controlled by the schemacrawler.format.identifier_quoting_strategy configuration property.
* Support for SQLite named foreign keys, auto-incremented columns, and more, with the latest JDBC driver for SQLite.
* Exclude database system schemas by default.
* Make -infolevel an optional command-line argument, and default to standard.
* Make -routines return no routines by default, to make it easier for first-time users.
* Show just SchemaCrawler information by default in the output, and not database and JDBC driver information. Add fine-tuned options for each.
* Adding some security lock-downs to XStream serialization.

## Release 14.16.04 - 2017-09-25
<a name="a14.16.04"></a>

* Updating dependencies.
* Bug-fix for Oracle routines.

## Release 14.16.03 - 2017-07-08
<a name="a14.16.03"></a>

* Updating dependencies.

## Release 14.16.02 - 2017-06-20
<a name="a14.16.02"></a>

* Bug fix for an issue with retrieving SQLite primary keys if the DDL used quoted names.
* Moved Sybase IQ plugin to new GitHub project.

## Release 14.16.01 - 2017-04-10
<a name="a14.16.01"></a>

* Better protection of user credentials in memory.
* Support for index remarks.
* Organize metadata views for extensibility. Please refer to http://sualeh.github.io/SchemaCrawler/data-dictionary-extensions.html
* Remove reliance on Java 8 features where they add an overhaead.

## Release 14.15.04 - 2017-04-02
<a name="a14.15.04"></a>

* Added foreign key retrieval strategies, and exposed overrides in configuration properties.

## Release 14.15.03 - 2017-03-28
<a name="a14.15.03"></a>

* Added table retrieval strategies, and exposed overrides in configuration properties.

## Release 14.15.02 - 2017-03-23
<a name="a14.15.02"></a>

* Clarified licensing terms.

## Release 14.15.01 - 2017-03-21
<a name="a14.15.01"></a>

* Fixed licenses published in jar files and on Maven Central.
* Cleaner description of column data types.

## Release 14.14.04 - 2017-03-11
<a name="a14.14.04"></a>

* Better and more efficient logging and error messages.
* Added short forms, -fmt for -outputformat command-line option, and -i for -infolevel.
* Made the output format command-line option optional. The output format is derived from the output file extension if it is not explicitly provided.

## Release 14.14.03 - 2017-02-23
<a name="a14.14.03"></a>

* Java utility class to parse enum values from column.getAttribute("COLUMN_TYPE"), for MySQL.
* Better error message for bad commands.

## Release 14.14.02 - 2017-02-08
<a name="a14.14.02"></a>

* To get the enum values from column.getAttribute("COLUMN_TYPE"), run SchemaCrawler for MySQL with -infolevel=maximum.
* New SchemaCrawler for SQLite utility, schemacrawler.tools.sqlite.SchemaCrawlerSQLiteUtility.

## Release 14.14.01 - 2017-01-11
<a name="a14.14.01"></a>

* The graph command is removed. Graphs are generated based on the output format.

## Release 14.12.01 - 2016-12-24
<a name="a14.12.01"></a>

* New command-line option to show weak associations, irrespective of the infolevel.
* Changed the boolean property, schemacrawler.format.hide_weak_associations to schemacrawler.format.show_weak_associations, since weak associations are hidden by default in SchemaCrawler output. Also added a new command-line switch, -weakassociations to show weak associations. Weak associations now can be shown with any infolevel setting.

## Release 14.11.02 - 2016-12-20
<a name="a14.11.02"></a>

* Support for the Teradata JDBC driver. The Teradata JDBC driver does not follow JDBC specifications. It should throw a SQLFeatureNotSupportedException for getFunctions, or at the least a SQLException state HYC00 ("Optional feature not implemented"). Instead, it throws with a state of HY000 ("General error").

## Release 14.11.01 - 2016-12-16
<a name="a14.11.01"></a>

* Show a better error message when a script file cannot be read.
* Handle SQLException when retrieving JDBC driver information.
* Extended copyright messages through 2017.

## Release 14.10.06 - 2016-10-29
<a name="a14.10.06"></a>

* Made OfflineSnapshotExecutable constructor public, so that offline snapshots can be loaded programatically.

## Release 14.10.05 - 2016-10-26
<a name="a14.10.05"></a>

* Fixed issue with generating SQLite diagrams.
* Distribution now contains an example SQLite database.
* Fail if the SQLite database does not exist.

## Release 14.10.04 - 2016-10-23
<a name="a14.10.04"></a>

* Fixed issue with loading of JDBC drivers.

## Release 14.10.03 - 2016-10-01
<a name="a14.10.03"></a>

* Fixed formatting of multi-line column remarks in HTML output.

## Release 14.10.02 - 2016-09-17
<a name="a14.10.02"></a>

* New lint to recommend that a surrogate key column be used as a primary key.
* Fixed lint that checks foreign key and primary key data types.
* Additional documentation on how to connect to Microsoft SQL Server with Windows authentication.

## Release 14.10.01 - 2016-08-31
<a name="a14.10.01"></a>

* Support for newlines in table and column comments.
* Ability to read default configuration settings from the classpath, from a file called schemacrawler.config.properties. This file is distributed in the lib/ folder.
* Removed command-line option -p to specify an additional configuration file.
* Added documentation on how to do programmatic diff of schemas on the website. Added code examples to the website.

## Release 14.09.03 - 2016-08-01
<a name="a14.09.03"></a>

* A new configuration options `schemacrawler.format.no_schema_colors` if you do not want to show catalog and schema colors.

## Release 14.09.02 - 2016-07-14
<a name="a14.09.02"></a>

* Allow primary keys to be retrieved in bulk, to speed up Oracle execution.

## Release 14.09.01 - 2016-06-29
<a name="a14.09.01"></a>

* The MariaDB driver interferes with the use of the MySQL driver, so it is no longer distributed.
* Fixing null pointer exception for table types, by introducing an unknown table type.

## Release 14.08.02 - 2016-05-16
<a name="a14.08.02"></a>

* Adding hidden columns in a separate list, accessed with a new method, Table.getHiddenColuns().
* Allow table constraint definitions to be obtained separately from table constraint information.

## Release 14.08.01 - 2016-05-04
<a name="a14.08.01"></a>

* Improved performance with Oracle, by substituting schemas.

## Release 14.07.08 - 2016-04-14
<a name="a14.07.08"></a>

* Removing linter for surrogate keys, since it is of dubious value.

## Release 14.07.07 - 2016-04-08
<a name="a14.07.07"></a>

* Added a new boolean property, schemacrawler.format.hide_weak_associations, to hide weak associations in SchemaCrawler output.

## Release 14.07.06 - 2016-03-26
<a name="a14.07.06"></a>

* Making SchemaCrawler available under the Eclipse Public License, in addition to the GPL and LGPL.
* Fixing ability to run SQL from the command-line.

## Release 14.07.05 - 2016-03-19
<a name="a14.07.05"></a>

* More detailed examples of how to use the SchemaCrawler API from script.
* Fixing a bug with the default linter threshold.

## Release 14.07.04 - 2016-03-16
<a name="a14.07.04"></a>

* Lint enhancements to dispatch.

## Release 14.07.03 - 2016-03-10
<a name="a14.07.03"></a>

* Add details of lints that were dispatched, before the dispatch takes place.

## Release 14.07.02 - 2016-02-27
<a name="a14.07.02"></a>

* Support for Compact Profile 2.
* Adding dispatch strategies for lint. This allows the SchemaCrawler command-line to fail with an exit code from the command-line when there are too many lints.

## Release 14.06.05 - 2016-02-14
<a name="a14.06.05"></a>

* Bug fix for bug while retrieving metadata.

## Release 14.06.04 - 2016-02-14
<a name="a14.06.04"></a>

* Complete support for Oracle hidden columns. Hidden columns are indicated with Table.isHidden(). Similarly, generated columns (including Oracle function-based columns) are indicated with Table.isGenerated(). You can get a complete list of all table columns, including hidden columns, with Table.getColumns(), if you have used the "maximum" schema info-level.

## Release 14.06.03 - 2016-02-10
<a name="a14.06.03"></a>

* Support for Oracle function-based indexes.

## Release 14.06.02 - 2016-02-10
<a name="a14.06.02"></a>

* Distributing correct versions of JDBC driver jars.
* Performance boost by using Oracle data dictionary query for table indexes. Idea courtesy of Patric Rufflar.

## Release 14.06.01 - 2016-02-08
<a name="a14.06.01"></a>

* Added table row counts in output and diagrams. Use schemacrawler.format.show_row_counts=true in the configuration file.
* Significant performance improvements in Java code, especially related to logging. Idea courtesy of Patric Rufflar.
* Performance boost by using Oracle data dictionary query for table columns. Idea courtesy of Patric Rufflar.

## Release 14.10.06 - 2016-02-03
<a name="a14.10.06"></a>

* Fixed logging for features that the JDBC driver does not support.
* Added utility to filter tables based on "raw" name.
* Updating JDBC drivers and dependencies.

## Release 14.05.04 - 2015-12-03
<a name="a14.05.04"></a>

* Fixed weak references, and logging for offline databases.

## Release 14.05.03 - 2015-12-02
<a name="a14.05.03"></a>

* Added new quickdump command, which works with -infolevel=minimum, though row order is not guaranteed.

## Release 14.05.02 - 2015-11-26
<a name="a14.05.02"></a>

* Externalized logic to quote and unquote database object identifiers.

## Release 14.05.01 - 2015-11-14
<a name="a14.05.01"></a>

* Fix default log level.
* Fix who uses SchemaCrawler list.
* Fix database plugin archetype.
* Sealing internal packages.

## Release 14.04.04 - 2015-10-31
<a name="a14.04.04"></a>

* Fix for retrieving PostgreSQL triggers.
* More efficient table column retrieval in IBM DB2.
* Added tests for unique constraints, and user-defined data types.
* Fixed output for base data-types.

## Release 14.04.03 - 2015-10-19
<a name="a14.04.03"></a>

* Overrides to foreign key retrieval, for more efficient IBM DB2 crawling.

## Release 14.04.02 - 2015-10-11
<a name="a14.04.02"></a>

* Detailed timing information in logs.

## Release 14.04.01 - 2015-10-11
<a name="a14.04.01"></a>

* Fixing broken -urlx switch.
* Reworked command-line processing, and database plugins.
* Removed Apache Derby database plugin. Apache Derby is now supported using a database URL only.
* Added MariaDB database plugin.

## Release 14.03.03 - 2015-09-20
<a name="a14.03.03"></a>

* An attempt at faster retrieval of foreign keys in Oracle.

## Release 14.03.02 - 2015-09-17
<a name="a14.03.02"></a>

* Better excludes for system schemas in Oracle.
* Added ability to report on tables causing a cycle in a lint, using Tarjan's algorithm.

## Release 14.03.01 - 2015-08-17
<a name="a14.03.01"></a>

* Added detailed lint help, which is available from the command-line, using -help -command=lint.
* Added configuration for LinterTooManyLobs.
* Added new lint for specifying a list of reserved words for column names.
* Added new lints for empty tables, tables where all columns are nullable, and tables with no primary keys.
* New SchemaCrawlerUtility methods to get database system specific functionality.
* Renaming CrawlHeaderInfo to CrawlInfo, and making it available in linters.
* Fixed issues with sorting tables and routines alphabetically. Sorting alphabetically also sorts by object type.

## Release 14.02.02 - 2015-08-14
<a name="a14.02.02"></a>

* Added new lints for empty tables, tables where all columns are nullable, and tables with no primary keys.

## Release 14.02.01 - 2015-08-10
<a name="a14.02.01"></a>

* Added a custom SQL Lint, to allow for data lints.
* New lint command-line option, -linterconfigs=<path> to specify the location of the linter XML configuration file.
* Ability to specify table inclusion rules for lints.

## Release 14.01.02 - 2015-07-28
<a name="a14.01.02"></a>

* Bug fix for connecting to a database using a URL.

## Release 14.01.01 - 2015-07-16
<a name="a14.01.01"></a>

* SchemaCrawler is now supported on Java SE 8 or better only.
* The SchemaCrawler project has been moved to GitHub.
* Database specific overrides are not not in SchemaCrawlerOptions, and can be passed in along with the database connection if needed.
* SchemaCrawler has a new project to demonstrate how to programatically diff schemas.
* New -title command-line argument to show a custom title on output and diagrams.
* Methods to get database objects by name are renamed to lookup, and return Optional. This allows calling code to handle nulls.
* Sequences are color-coded by schema like the rest of the database objects.
* Rename indices to indexes consistently in method names.
* All references to parent objects are soft references, to allow for more efficient memory management and serialization.
* All references to parent objects are soft references, to allow for more efficient memory management and serialization.
* Shell scripts sc.sh and sc.cmd renamed to schemacrawler.sh and schemacrawler.cmd, since they clash with system utilities.

## Release 12.06.03 - 2015-03-27
<a name="a12.06.03"></a>

* Showing cardinality for tables that are filtered out.

## Release 12.06.02 - 2015-03-26
<a name="a12.06.02"></a>

* Fixing links on website.
* Fixing diagram to show hanging arrows for referenced tables.

## Release 12.06.01 - 2015-03-24
<a name="a12.06.01"></a>

* Hyperlinks in HTML output from foreign keys to tables.
* Lighter colors for tables, consistent in both HTML and graph output. Colors are assigned per catalog and schema.
* Foreign key cardinality in HTML and text output.
* New command-line option to remove empty tables (with no rows of data) from output -hideemptytables
* Not showing database password in command-line logs.
* Showing database server and JDBC driver in logs.

## Release 12.05.02 - 2015-03-17
<a name="a12.05.02"></a>

* Better identification of weak associations.
* Showing routine parameter names in JSON.

## Release 12.05.01 - 2015-03-14
<a name="a12.05.01"></a>

* Better identification of weak associations.

## Release 12.04.02 - 2015-02-10
<a name="a12.04.02"></a>

* Fix bug with connecting to Oracle.

## Release 12.04.01 - 2015-01-18
<a name="a12.04.01"></a>

* Reverting back to SQLite JDBC driver version 3.7.8, since that is the most stable, even if not readily available.
* Refactored API for reading input files, such as scripts.
* Made compressed file format the default for offline snapshots.

## Release 12.03.02 - 2015-01-18
<a name="a12.03.02"></a>

* Removed the -driver command-line option, since most JDBC drivers will register themselves automatically.
* Made sure that a JDBC driver for a database server is available, before the -server command-line option is valid for that server.

## Release 12.03.01 - 2015-01-02
<a name="a12.03.01"></a>

* Added -noremarks command-line option to hide table and column remarks (or comments).
* Using a stable version of the Xerial SQLite JDBC driver that is readily available on Maven Central. This does not support retrieving primary key names.
* Removed built-in support for ant tasks, since there is not way to omit an option if one is not specified.
* Changed copyright to 2015.
* New parser for command-line options.

## Release 12.02.03 - 2014-12-25
<a name="a12.02.03"></a>

* Integration with Thymeleaf templating.
* Flushing of output.

## Release 12.02.02 - 2014-12-24
<a name="a12.02.02"></a>

* Better logging of execution.
* Quicker execution when routines are not output.

## Release 12.02.01 - 2014-12-21
<a name="a12.02.01"></a>

* New output format, htmlx, which embeds database diagrams into an HTML file. The diagrams are in SVG format.
* Converted all HTML output to HTML5.
* Null-checks now throw NullPointerException, instead of IllegalArgumentException.
* Tested support of international character sets in table names and column names.
* Converting filter classes to Predicate, to prepare for Java 8 support.
* Converted all file operations to use Java Path, for better error handling.

## Release 12.01.01 - 2014-11-27
<a name="a12.01.01"></a>

* Collapsed distribution into a single distribution download, with Apache Ant Ivy scripts for downloading additional dependencies. A new -server command-line option will connect to a database with built-in support.
* Added Maven archetypes for database server plugins.
* Building Debian package as part of the main distributable.
* Added support for the H2 Database Engine.
* Changes direction of arrow for foreign keys in text output, to always point to the primary key.
* Made consistent CSS stylesheets for examples, website, and HTML text output.
* Fixed issues with retrieving functions from H2 Database Engine.
* Standardized CSS styles between the website, example readmes, and SchemaCrawler HTML output.

## Release 11.02.01 - 2014-11-04
<a name="a11.02.01"></a>

* Added options to hide cardinality in graphs.
* Added Maven archetypes for plugins.

## Release 11.01.01 - 2014-09-21
<a name="a11.01.01"></a>

* Changed Maven groupId to us.fatehi.
* Renamed Database class to Catalog.
* Changed all output formats to include table and column remarks whenever available.
* Added command-line option options to show significant columns, such as primary and foreign key columns, and columns that are part of unique indexes. Use the `-infolevel=standard -command=brief` command-line options.
* Indicate auto-incremented columns in graph and other output.

## Release 10.10.05 - 2014-08-04
<a name="a10.10.05"></a>

* Fixed bug with weak associations in diagrams being drawn twice.
* Added more variations of diagrams to website.

## Release 10.10.04 - 2014-07-27
<a name="a10.10.04"></a>

* Fixed bug with cardinality in diagrams, when a foreign key is only a part of the primary key of a table.
* Fixed -sequences command-line argument, and got it to work correctly with the properties file.

## Release 10.10.03 - 2014-07-06
<a name="a10.10.03"></a>

* Added support for include and exclude expressions for synonyms and sequences in config properties files.

## Release 10.10.02 - 2014-07-02
<a name="a10.10.02"></a>

* Added support for auto-incremented columns, and generated columns.

## Release 10.10.01 - 2014-06-22
<a name="a10.10.01"></a>

* Added support for sequences.

## Release 10.09.01 - 2014-05-14
<a name="a10.09.01"></a>

* Fixed table type to be based on a string, not an enum. See http://sourceforge.net/p/schemacrawler/discussion/495990/thread/09186044 for details.
* Changed the object inheritance of a privilege, so that it is not a database object.

## Release 10.08.05 - 2014-04-25
<a name="a10.08.05"></a>

* Fixed lint "foreign key with no index" to look at primary keys as well.
* Fixed bug #31, https://sourceforge.net/p/schemacrawler/bugs/31/, for loading XML databases.
* Fixed MySQL connection issue, by removing URL parameter "useInformationSchema=true" so that MySQL's buggy metadata provider is not used. This provider does not honor mixed-case tables names.

## Release 10.08.04 - 2014-04-03
<a name="a10.08.04"></a>

* Column sort options sort columns in dumps also.
* Added generation date to database diagram.

## Release 10.08.03 - 2014-03-24
<a name="a10.08.03"></a>

* Made extra columns provided in all information schema views available as attributes of SchemaCrawler Java objects.
* Added table definition metadata for Oracle.
* Allow for code to be executed before and after the crawl. This is in the form of pre- and post- executables.
* Added foreign key definition metadata for Oracle.
* Fixed DDL metadata for Oracle, so that it is portable.
* Converted code to Git.

## Release 10.08.02 - 2013-12-28
<a name="a10.08.02"></a>

* Updated copyright notices.
* Fixed javadocs, using standard Java 7 stylesheet.

## Release 10.08.01 - 2013-11-17
<a name="a10.08.01"></a>

* Trim all table names, to allow Informix to match correctly.

## Release 10.8 - 2013-11-10
<a name="a10.8"></a>

* Providing richer information about table constraints, including details of constrained columns.

## Release 10.7 - 2013-11-05
<a name="a10.7"></a>

* Providing richer information about table constraints. Renaming check constraints to table constraints.
* Worked abound ArrayIndexOutOfBoundsException in MySQL connector with connector 5.1.26 and server version 5.0.95\. See https://sourceforge.net/p/schemacrawler/patches/5/

## Release 10.6 - 2013-10-30
<a name="a10.6"></a>

* Changed InclusionRule to an interface, to allow custom implementations.
* Fixed lack of support for type maps by the Sybase IQ database driver.

## Release 10.5 - 2013-09-24
<a name="a10.5"></a>

* Fixed bug with table restrictions.
* Support all command-line options in ant task.

## Release 10.4 - 2013-07-24
<a name="a10.4"></a>

* Fixed view definitions for PostgreSQL.
* Fixed retrieving index metadata for tables, working around PostgreSQL bugs #3480 and #6253.
* Added retrieving index definitions for PostgreSQL and Oracle.
* Added retrieving view definitions for Apache Derby.

## Release 10.3 - 2013-05-24
<a name="a10.3"></a>

* Rework of the column data type classes, as well as a workaround for an Oracle JDBC driver bug with reporting data type information.

## Release 10.2 - 2013-05-21
<a name="a10.2"></a>

* New option to set output encoding format. The option is "schemacrawler.encoding.output" in the SchemaCrawler properties file. Default input and output encoding has been changed to UTF-8.
* Support for showing trigger information in Sqlite.

## Release 10.1 - 2013-04-21
<a name="a10.1"></a>

* SchemaCrawler support for J2SE 7 only, since previous Java versions are deprecated. Converted the codebase over to use J2SE 7 constructs.
* New command-line option, -only-matching, to show only matching tables, and does not show foreign keys that reference other non-matching tables. Modeled after the analogous grep option.
* New option to set input encoding format for script files and templates. The option is "schemacrawler.encoding.input" in the SchemaCrawler properties file.
* Renamed command-line option -table_types to -tabletypes.

## Release 9.6 - 2013-03-08
<a name="a9.6"></a>

* Fixed diagram generation on Linux when no GraphViz options are supplied.
* Added trigger names to JSON output by default. Added new configuration variable, schemacrawler.format.hide_trigger_names to suppress trigger names in all output formats.
* Added new -portablenames command-line option, to allow for easy comparison between databases, by hiding foreign key names, constraint names, trigger names, index and primary key names, and not showing the fully-qualified table name.

## Release 9.5 - 2013-02-24
<a name="a9.5"></a>

* Updated HTML generation to have captions consistently for all tables. Numeric data is right-aligned in HTML, and correctly reported in JSON as well. These changes affect other output formats as well.
* Updated Oracle database connection URL to the new syntax.

## Release 9.4 - 2013-02-02
<a name="a9.4"></a>

* Fix for honoring text formatting options in GraphViz dot output.

## Release 9.3.2 - 2012-11-17
<a name="a9.3.2"></a>

* Fix for honoring sort options on JSON output.

## Release 9.3.1 - 2012-11-3
<a name="a9.3.1"></a>

* Fix for generating diagrams on Unix.

## Release 9.3 - 2012-10-31
<a name="a9.3"></a>

* Weak associations are returned sorted.
* Added ability to specify additional command-line options for GraphViz, using either Java system properties, or environmental variables.

## Release 9.2 - 2012-10-21
<a name="a9.2"></a>

* Fixed Windows script, schemacrawler.cmd.
* Better support for sqlite, including the latest database driver, reporting of foreign keys, and view definitions.
* Updated the jTDS JDBC driver to 1.2.6, for Microsoft SQL Server.
* Deprecated the getType() method on database objects.

## Release 9.1 - 2012-09-30
<a name="a9.1"></a>

* Added examples for Groovy, Ruby and Python scripting.
* SchemaCrawler is now bundled as an OSGi jar.

## Release 9.0 - 2012-09-08
<a name="a9.0"></a>

* The API has changed from 8.x versions. All API calls return collections instead of arrays. Schemas are not containers, but tags for database objects. Tables and routines (stored procedures and functions) are available directly on the database, and there are new finders to search by schema.
* Support for database functions has been added. All references to stored procedures in the code and documentation now refer to routines. Stored procedures and functions are treated in the same way, as routines.
* SchemaCrawler now allows queries to be specified on the command-line. If a command is not recognized, or is not a named query, it is executed as if it is a query.
* The output format includes trigger definitions as part of the schema, instead of as the detailed schema output.
* Database connections have to be specified on the command-line, by providing the driver class name and URL. Database connections can no longer be defined in properties files.
* There are new command-line options. -V, --version will print the SchemaCrawler version and exit. There are shorter and more standard (Unix-like) synonyms -u (for -user), -c (for -command), and -o (for -output). The -procedures switch has been renamed -routines.

## Release 8.17 - 2012-07-12
<a name="a8.17"></a>

* The HTML output format outputs valid HTML5 and CSS3, as valid XML.
* JSON output supports all of the formatting options that the other text formats support.
* A new method, getShortName(), get the unqualified name for columns and other dependent objects.
* A new text format option, "schemacrawler.format.show_unqualified_names", shows the unqualified name in text output, for easier comparisons across different catalogs and schemas.

## Release 8.16 - 2012-06-20
<a name="a8.16"></a>

* Added support for synonyms. Synonyms are shown in SchemaCrawler output for IBM DB2, Oracle, and Microsoft SQL Server, as well as available for extension in other databases.

## Release 8.15 - 2012-05-05
<a name="a8.15"></a>

* Added distribution for Sybase IQ.

## Release 8.14 - 2012-04-08
<a name="a8.14"></a>

* Added new command-line switch to show parent and child tables, in addition to those selected by grep.

## Release 8.12 - 2012-02-25
<a name="a8.12"></a>

* Added ability to stream SchemaCrawler output.

## Release 8.11 - 2012-02-18
<a name="a8.11"></a>

* Added ability to configure SchemaCrawler linters.
* Added a new lint for useless surrogate keys.
* Added ability to tag tables and columns with additional metadata attributes from SQL queries.

## Release 8.10 - 2011-12-08
<a name="a8.10"></a>

* Added SchemaCrawler Lint as a separate report, with ability to add custom linters.
* Added additional SchemaCrawler linters.
* Added ability to extend SchemaCrawler with custom command.
* Fixed issue with constraints with the same name in different schemas.

## Release 8.9 - 2011-12-08
<a name="a8.9"></a>

* Added SchemaCrawler Lint as a separate report, with ability to add custom linters.
* Added additional SchemaCrawler linters.
* Added ability to extend SchemaCrawler with custom command.
* Fixed issue with constraints with the same name in different schemas.

## Release 8.8 - 2011-11-05
<a name="a8.8"></a>

* Added JSON and TSV output formats.
* Moved SchemaCrawler Maven Plugin into a new SourceForge project.

## Release 8.7 - 2011-09-05
<a name="a8.7"></a>

* New INFORMATION_SCHEMA views to provide Oracle metadata details.
* Update database driver versions.
* Fixed bug #3392557 - NullPointer exception thrown when columns excluded in Oracle.
* Fixed bug #3392976 - Exception retrieving tables: Could not retrieve indexes for Oracle schemas with $ in the name.
* Fixed bug #3401752 - Attempt to locate database 'dbo' by name failed.

## Release 8.6 - 2011-05-08
<a name="a8.6"></a>

* New feature to chain, or run multiple SchemaCrawler commands using JavaScript.

## Release 8.5.1 - 2011-01-28
<a name="a8.5.1"></a>

* Re-organized command-line code, including grep command-line.

## Release 8.5 - 2011-01-28
<a name="a8.5"></a>

* Changed copyright to 2011.
* Fixed grep.
* Removed option to provide index info SQL.
* Better unit test coverage. Restructuring of Eclipse projects.

## Release 8.4 - 2010-12-23
<a name="a8.4"></a>

* Simplified commands - list_objects is now simply list. Removed the -show_stored_procedures command-line option - simply use -proedures=
* Fixed issue with obtaining PostgreSQL tables.
* Fixed issue with obtaining Derby foreign keys.
* Made -infolevel a required command-line argument.
* Use Maven 3.0 in the build.

## Release 8.3.3 - 2010-08-01
<a name="a8.3.3"></a>

* Added color to headings in HTML output.
* Fixed bug when remarks header was not shown if the table did not have remarks, but columns did.
* DB2 INFORMATION_SCHCEMA SQL fixed.
* Foreign keys show full column names in graphs, even when the table is excluded.

## Release 8.3.2 - 2010-08-01
<a name="a8.3.2"></a>

* Column remarks are printed along with table remarks.
* Foreign keys show full column names in graphs, even when the table is excluded.

## Release 8.3.1 - 2010-07-18
<a name="a8.3.1"></a>

* Added new command-line option, -urlx, to pass JDBC URL properties to bundled distributions.
* Added details on the "main" command-line in the how-to section of the website.

## Release 8.3 - 2010-07-15
<a name="a8.3"></a>

* Added new bundled distribution for IBM DB2.
* Added new bundled distribution for HyperSQL 2.0.0.
* Added support for spaces in schema and catalog names.
* Changed the test database to HyperSQL 2.0.0\. Changed the test data, and added more robust unit tests.

## Release 8.2 - 2010-05-25
<a name="a8.2"></a>

* Added support for spaces or reserved words in table, procedure, and column names.
* Added new database lint to find column and table names with spaces or reserved words in them.
* Added SchemaCrawler to the Sonatype Maven public repository. Added new Maven projects examples.
* Table and procedure remarks are output with verbose output.
* The Maven plug-in generates a database report that uses the same stylesheet as the rest of the Maven generated website.
* Generated database diagrams are less "colorful".

## Release 8.1 - 2010-02-28
<a name="a8.1"></a>

* Added SchemaCrawler lint, to highlight potential normalization issues, such as incrementing column names, and other issues. A new -infolevel value, lint, was added.
* Weak associations are now available only with SchemaCrawler lint.

## Release 8.0 - 2010-02-03
<a name="a8.0"></a>

* Added the -infolevel command-line switch to control the amount of database metadata retrieved by SchemaCrawler.
* Create a command registry, which allows pluggable commands. Any command can be run from the main class.
* Dropped the -catalogs command-line switch filtering catalogs. Dropped the concept of a catalog from the API. Databases now directly contain schemas.

## Release 7.6 - 2009-12-03
<a name="a7.6"></a>

* Fixed specification of Oracle SID using the -database command-line switch, as well as specification of port number.
* Fixed shell script classpath.

## Release 7.5.1 - 2009-11-28
<a name="a7.5.1"></a>

* Added methods to get imported and exported foreign keys and weak associations.

## Release 7.5 - 2009-11-25
<a name="a7.5"></a>

* Added a SchemaCrawlerInfo object to the database metadata, to provide the SchemaCrawler version number, and Java system properties.
* Updates to all bundled database drivers.
* Bug-fix for obtaining MySQL foreign keys.
* Added ability to script the database, using JavaScript. A live connection is provided to the JavaScript context for this purpose.
* Removed command-line switch, -schemapattern, and the schemapattern option. Use the -schemas command-line switch instead.
* Added sections to SchemaCrawler output, to make it more readable.
* Tables sort is done using graph algorithms, and includes cycle checks.

## Release 7.4 - 2009-09-24
<a name="a7.4"></a>

* Added new pre-packaged SchemaCrawler for Oracle.
* Fixed issue with index sort order of primary key columns.
* Updated website with new front page.

## Release 7.3.1 - 2009-09-24
<a name="a7.3.1"></a>

* Enhancement to the JavaSqlType class, to contain the SQL type group. New lookup by data-type name added to JavaSqlTypesUtility.
* Removed method, getTypeClass(), from ColumnDataType.
* Removed deprecated methods for grouping SQL data types (such as isReal()) from the ColumnDataType class.

## Release 7.3 - 2009-09-19
<a name="a7.3"></a>

* Added support for mapping of SQL data type to Java classes. This includes the new JavaSqlType class, and the JavaSqlTypesUtility. A new method, getTypeClass(), has been added to ColumnDataType.
* Added SchemaCrawler for Apache Derby, that is packaged for Apache Derby.

## Release 7.2 - 2009-08-21
<a name="a7.2"></a>

* Better modeling of privileges and grants.
* Bug fix for obtaining indexes and foreign keys with the Oracle driver.
* Added natural sort order for tables, based on the foreign keys, and corresponding new configuration option, and command-line switch, -sorttables. Also added new API methods to get child and parent tables, based on the foreign keys.
* Handle null schemas for databases that do not support schemas, such as MySQL.
* Allow data dumps when tables contain binary objects, by fixing the SELECT sort order.

## Release 7.1 - 2009-08-12
<a name="a7.1"></a>

* Better log messages, including for database connections, and inclusion of table and columns in the output.
* More efficient use of database connections, including a change to the Executable.execute method to take a database connection, rather than a data-source.
* Ensure that the pooled connection is closed, rather than the original database connection. This allows SchemaCrawler to play well with pooled connections.

## Release 7.0 - 2009-07-10
<a name="a7.0"></a>

* Major restructuring of the API, with the introduction of a Database object that contains all of the database metadata, and containing catalog and schema objects.
* To allow for more control on table selection, the -tables command-line parameter matches to the fully qualified table name, CATALOG.SCHEMA.TABLENAME. Similarly for the -routines command-line parameter.

## Release 6.4 - 2009-05-10
<a name="a6.4"></a>

* New pre-packaged distribution, SchemaCrawler for SQLite.
* Enhanced algorithm for finding weak associations.

## Release 6.3 - 2009-05-08
<a name="a6.3"></a>

* If no commands are specified, defaults to standard_schema, which provides the most commonly needed details of the schema.
* SchemaCrawler Grep functionality is built into the SchemaCrawler options, and the SchemaCrawler command-line. This means that all of the SchemaCrawler commands, including graphing, will use filtering and grep options.
* SchemaCrawler command-line option help has been re-written. New command-line options are available.
* SchemaCrawler can produce Graphviz DOT files to generate schema diagrams.

## Release 6.2 - 2009-04-08
<a name="a6.2"></a>

* A new feature to infer weak associations between tables, even if there is no foreign key. Ruby on Rails table schemes are supported, as well as other simple relationships. Table name prefixes are automatically detected.
* Database diagram support using Graphviz. Primary keys are indicated in a darker color than other columns. Foreign keys are indicated with arrow connectors. Weak associations between tables are shown in dashed lines. Multiple output formats are supported. Support for Jung is dropped.

## Release 6.1 - 2009-03-29
<a name="a6.1"></a>

* Better reporting of ascending and descending index columns. This is an API change.
* Better grep options, to allow searching through both tables and stored procedures.
* Grep options default to excluding stored procedures.
* Minor bug fixes for Sybase, including generating warnings when obtaining table column privileges, instead of failing.
* New Windows script file for locating Java, and launching SchemaCrawler.
* Reorganization of distribution, so that only a single SchemaCrawler jar file is distributed in all distributions. License distribution is also reorganized.

## Release 6.0.2 - 2008-11-30
<a name="a6.0.2"></a>

* Minor bug fixes for MySQL, where the schema name was being reported as null, causing an exception.

## Release 6.0.1 - 2008-10-30
<a name="a6.0.1"></a>

* Minor bug fixes for Oracle, where the data-type was a short instead of an integer.

## Release 6.0 - 2008-10-16
<a name="a6.0"></a>

* Changed the API so that there is a separation of concerns - the SchemaInfoLevel should be an option, not a CrawlHandler property.
* Changed the API so the top level object is a catalog, which contains schemas. This means that there is full support for multiple schemas.
* Added support for JavaScript scripting.

## Release 5.5 - 2008-02-28
<a name="a5.5"></a>

* Fixed bug that always reported an index as unique.
* Fixed bug with primary key columns.
* SchemaCrawler for Microsoft SQL Server to show procedures and triggers.
* SchemaCrawler distribution for any database.

## Release 5.4 - 2007-12-15
<a name="a5.4"></a>

* Added new command-line switch, -schemapattern, for specifying schema patterns when a connection is specified from the command-line.
* Added object attributes automatically, from unused columns in the metadata calls. Nothing goes to waste.
* Added JDBC driver metadata, including driver properties, and separated those from the database system metadata.

## Release 5.3 - 2007-11-15
<a name="a5.3"></a>

* Made sure that all relevant license files are getting delivered.
* Fixed bug in accessing foreign key update and delete rules on Oracle.
* Fixed equals comparison on all schema objects, and added a unit test for equals.
* Added an easier way to access foreign keys from the foreign key column itself Use isPartOfForeignKey(), and getReferencedColumn() on the Column class.
* Externalized the wrapper for java.sql.Types into a new public class, schemacrawler.schema.SqlDataType.
* Added the ability to get result-set metadata.
* Changed schema info level from an enum into a class that can be used to specify details on what aspects of the schema need to be retrieved.

## Release 5.2 - 2007-10-30
<a name="a5.2"></a>

* A significant re-write of the internal command processing code.
* Spring framework support for all of SchemaCrawler functionality, including integrations and grep.
* New example for grep.
* New example for using the Spring framework with SchemaCrawler.
* Pre-packaged releases do not load config file, even if available.
* Fixed Unix shell script for PostgreSQL.

## Release 5.1 - 2007-08-05
<a name="a5.1"></a>

* Simplified examples.
* Executable for SchemaCrawler for Microsoft SQL Server and MySQL.
* Commonly available SchemaCrawler command-line argument for setting the log level.

## Release 5.0 - 2007-06-25
<a name="a5.0"></a>

* API changed to use Java 5 constructs. Tested with Java 6\. API is no longer Java 1.4 compatible.
* Started SchemaCrawler Grep for MS SQL Server.
* Fixed java.lang.AbstractMethodError with Java 6.

## Release 4.2 - 2007-01-07
<a name="a4.2"></a>

* Fixed null pointer exception with DB2.
* Fixed error message on a bad command-line. Added a general -help option.
* Added new schema grep functionality, that can search for tables and columns usng regular expressions.
* Added new example for setting options programatically.
* Added documentation for providing database connection options directly on the command-line.
* Updated ant task to optionally take database connection options as arguments, instead of from the configuration file.

## Release 4.1 - 2006-12-07
<a name="a4.1"></a>

* Fixed bug [ 1610140 ] Sorting doesn't work.
* Fixed output of standard data types. Also changed the configuration option name.
* Fixed column output for tables and procedures that contain an underscore in their name. JDBC matches the underscore to a wildcard character.

## Release 4.0 - 2006-12-03
<a name="a4.0"></a>

* Added support for SQL standard INFORMATION_SCHEMA views. If equivalent SQL for the standard INFORMATION_SCHEMA view is specified, it is used to provide additional schema information such as check constraints, triggers, and view and procedure definitions.
* Added support for get index information views. This SQL will be used in preference to the getIndexInfo JDBC method to allow for databases and drivers that do not support getIndexInfo due to permissioning issues.
* Better output format for HTML and text.
* Better handling of output file resources.
* Better handling of database connection resources.
* More information on the website, and better website usability.

## Release 3.8 - 2006-09-26
<a name="a3.8"></a>

* Added Maven 2.0 plugin for generation of schema reports
* Added command-line option to provide database connection information
* Worked around a bug in the Oracle driver as described in http://issues.apache.org/jira/browse/DDLUTILS-29?page=all

## Release 3.7 - 2006-06-12
<a name="a3.7"></a>

* Added retrieval of database properties
* Added retrieval of column data types, both system datatypes and UDTs
* Added unit tests to ensure that the XHTML output is valid XML
* Fixed bug with merge rows option
* Fixed bug with appending output when multiple runs of SchemaCrawler append to the same output file
* Fixed SQLException with Oracle Database 10g Express Edition driver on outputting views
* Added XStream integration for XML serialization and deserialization
* Added new command, verbose_schema, that can give all possible details of the schema
* Obtain database system properties when the maximum schema command is used
* Added integrations with FreeMarker for templated output
* Removed Betwixt integration, since the required functionality is provided by XStream

## Release 3.6 - 2006-03-28
<a name="a3.6"></a>

* Added integrations with Velocity for templated output, and with JUNG for JPEG graph generation
* Added new examples to demonstrate Velocity templating, and JUNG graphs
* New command-line option to specify the output file name

## Release 3.5 - 2006-03-06
<a name="a3.5"></a>

* Added ability to retrieve column and table privileges
* Added Betwixt mappings for XML serialization and de-serialization
* Added Unix shell scripts for examples
* Fixed bug with the ordering of primary key and index columns
* Changed API - ColumnInfoLevel is called SchemaInfoLevel
* Moved source repository to Subversion

## Release 3.4 - 2006-01-16
<a name="a3.4"></a>

* Correction of spelling of the "sort_alphabetically" option names
* Refactoring of inheritance hierarchy of database objects, resulting in fewer equals, hashCode and compareTo implementations
* Better HTML formatting for counts
* Dirty reads are performed for counts and other operations
* Sets the default value for table columns
* Javadoc clean-up
* Findbugs, checkstyle, and PMD code smells clean-up

## Release 3.3 - 2005-11-03
<a name="a3.3"></a>

* The ability to include as well as exclude tables using regular expressions.
* The ability to include as well as exclude columns using regular expressions.
* A new substitution variable, ${columns}, for use in database queries that iterate over tables.
* New examples for executing database-specific and user-defined queries have been added.
* Better output formatting for HTML dumps, and counts in text format.
* New website.
* Worked around an issue with the Sybase driver, that provides primary keys in a different order than most other drivers.
* Improved logging, and made the log level in the configuration really work.


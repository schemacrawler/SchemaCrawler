# Database System Support

SchemaCrawler supports almost any database that has a JDBC driver. You can simply download a JDBC driver, and place it in the `lib` directory. SchemaCrawler will pick it up immediately.

SchemaCrawler is bundled with JDBC drivers for some commonly used relational database management systems (RDBMS) for convenience. The bundled distributions of SchemaCrawler are ready to use for a given database system. However, some JDBC drivers are proprietary, even if free. These JDBC drivers need to be downloaded separately.

The JDBC drivers for database systems commonly used with SchemaCrawler are included with the SchemaCrawler download:

- [SQLite](https://www.sqlite.org/) [Xerial SQLite JDBC driver](https://github.com/xerial/sqlite-jdbc)
- [Oracle](https://www.oracle.com/) [JDBC driver](https://www.oracle.com/technology/software/tech/java/sqlj_jdbc/index.html)
- [Microsoft SQL Server](https://www.microsoft.com/sqlserver/) [JDBC driver](https://github.com/Microsoft/mssql-jdbc)
- [IBM DB2](https://www.ibm.com/software/data/db2/) [JDBC driver](https://www.ibm.com/support/pages/db2-jdbc-driver-versions-and-downloads)
- [MySQL](https://www.mysql.com/) [Connector/J JDBC driver](https://dev.mysql.com/downloads/connector/j/)
- [PostgreSQL](https://www.postgresql.org/) [JDBC driver](https://jdbc.postgresql.org/)
- [Trino](https://trino.io/) [JDBC driver](https://trino.io/docs/current/client/jdbc.html)
- [Cassandra](https://cassandra.apache.org/_/index.html) [JDBC driver](https://github.com/ing-bank/cassandra-jdbc-wrapper)
- Offline database support does not need any JDBC driver

For any other database that includes a compliant JDBC driver, place the JDBC in the
SchemaCrawler `lib` directory. [Amazon Aurora](https://aws.amazon.com/rds/aurora/) is supported
in MySQL and PostgreSQL modes.


## Additional SchemaCrawler Database Plugins

Some databases such as Oracle TimesTen and SAP IQ need an additional SchemaCrawler database
plugins, which are available from the
[schemacrawler/SchemaCrawler-Database-Plugins](https://github.com/schemacrawler/SchemaCrawler-Database-Plugins)
project. _Please note that these plugins are unsupported._ If you would like support, please
follow the instructions on the [Consulting](consulting.html) page.


## How To

SchemaCrawler provides database support in two ways. If you are connecting to a database
not mentioned above, you can simply provide the database connection URL, a username and password.

For the databases mentioned above, you can provide connection details by using the following
command-line options:

- `--server` - identifies the database server, and can be one of `sqlite`, `oracle`, `sqlserver`,
   `db2`, `mysql`, `postgresql`, `offline`
- `--database` - identifies the database, and can have different meaning based on the server type
- `--host` - specifies the database server host; it is optional, and defaults to localhost
- `--port` - specifies the database server port; it is optional, and defaults to the default port for the server type

For example, typical command-line options for SchemaCrawler for Microsoft SQL Server looks like:

```
--server=sqlserver \
--host=db.example.com \
--port=1433 \
--database=schemacrawler \
--schemas=schemacrawler.dbo \
--user=schemacrawler \
--password=schemacrawler
```

You should always use the `--schemas` command-line switch for databases that support it. The value
for the `--schemas` switch is a regular expression that determines which schemas SchemaCrawler will
work with. The "schema" is database-dependent - for example, on Microsoft SQL Server, typically
schemas look like "database_name.user", but for Oracle, typically, schemas look like "USER" (in uppercase).

If a system has environmental variables that contain a value, you can use the supported shell functionality to pass data to SchemaCrawler. For example, `--host %DBHOST%` on Windows will use the host value specified in the `DBHOST` environmental variable, and `--host $DBHOST` will do the same thing on Linux.

## Making Connections to a Database

### Microsoft SQL Server

You need to specify the host, port, database name, and the schemas you
are interested in, for Microsoft SQL Server.


Typical command-line arguments will look like:

```
--server=sqlserver \
--host=db.example.com \
--port=1433 \
--database=schemacrawler \
--schemas=schemacrawler.dbo \
--user=xxxxx \
--password=xxxxx \
--info-level=standard \
-command=schema
```

You can also pass connection properties using the `--urlx` command-line switch.

If your Microsoft SQL Server instance is set up with instance names, named pipes, or Windows authentication, you
will need to use a database connection URL. See the
[documentation for the Microsoft JDBC Driver for SQL Server](https://docs.microsoft.com/sql/connect/jdbc/connecting-to-sql-server-with-the-jdbc-driver)
for details. If you are using the URL version of the command-line, it may be a good idea to include the database name as one of
the connection URL property (`databaseName`).

Typical command-line arguments for connecting to SQL Server with Windows authentication will look like:

```
--server=sqlserver \
--url=jdbc:sqlserver://db.example.com:1433;databaseName=master;encrypt=false \
--schemas=schemacrawler.dbo \
--user= \
--password= \
--info-level=standard \
--command=schema
```

or

```
--server=sqlserver \
--host=db.example.com \
--port=1433 \
--urlx=integratedSecurity=true \
--database=schemacrawler \
--schemas=schemacrawler.dbo \
--user=xxxxx \
--password=xxxxx \
--info-level=standard \
-command=schema
```

Please make sure that you use the `--schemas` option to reduce the number of schemas in the output. In the
SchemaCrawler interactive shell, use the `limit` command with this option.

### Oracle

You need to specify the host, port, Oracle Service Name, and the schemas you
are interested in, for Oracle.

You can use a query similar to `SELECT GLOBAL_NAME FROM GLOBAL_NAME`
to find the Oracle Service Name.

Typical command-line arguments will look like:

```
--server=oracle \
--host=db.example.com \
--port=1521 \
--database=ORCL \
--schemas=SCHEMACRAWLER \
--user=xxxxx \
--password=xxxxx \
--info-level=standard \
--command=schema
```

In the example above, "ORCL" is the Oracle Service Name.


### MySQL

You need to specify the host, port, database name, and the schemas you
are interested in, for MySQL.


Typical command-line arguments will look like:

```
--server=mysql \
--host=db.example.com \
--port=3306 \
--database=schemacrawler \
--schemas=schemacrawler \
--user=xxxxx \
--password=xxxxx \
--info-level=standard \
--command=schema
```

### PostgreSQL

You need to specify the host, port, database name, and the schemas you
are interested in, for PostgreSQL.


Typical command-line arguments will look like:

```
--server=postgresql \
--host=db.example.com \
--port=5432 \
--database=schemacrawler \
--schemas=public \
--user=xxxxx \
--password=xxxxx \
--info-level=standard \
--command=schema
```

- `--host`  is optional if the `PGHOSTADDR` or `PGHOST` environmental variables are set
- `--port`  is optional if the `PGPORT` environmental variables is set
- `--database`  is optional if the `PGDATABASE`  environmental variables is set


### MariaDB

You need to specify the database connection URL, and the schemas you are
interested in, for MariaDB. First make sure that the MariaDB driver is
in the `lib/` folder.


Typical command-line arguments will look like:

```
--url=jdbc:mariadb://db.example.com:3306/schemacrawler \
--schemas=schemacrawler \
--user=schemacrawler \
--password=schemacrawler \
--table-types=UNKNOWN,VIEW \
--info-level=standard \
--command=schema
```


## Unconventional Data Sources

Since SchemaCrawler simply uses a conformant JDBC database driver, it can access the schemas of unconventional data sources such as Airtable, Salesforce, or even say Google Calendar.  Take a look at the rich variety of JDBC drivers from [cdata](https://www.cdata.com/drivers/).

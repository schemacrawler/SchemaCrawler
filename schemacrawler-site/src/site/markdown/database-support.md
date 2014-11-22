# Database System Support

SchemaCrawler supports almost any database that has a JDBC driver. You can simply download a JDBC 
driver, and place it in the `lib` directory. SchemaCrawler will pick it up immediately. 

SchemaCrawler is bundled with JDBC drivers for some commonly used relational database management 
systems (RDBMS) for convenience. The bundled distributions of SchemaCrawler are ready to use for a 
given database system. However, some JDBC drivers are proprietary, even if free. These JDBC 
drivers need to be downloaded separately.

The JDBC drivers bundled with SchemaCrawler are:

- The [SQLite](http://www.sqlite.org/) [Xerial SQLite JDBC driver](http://www.xerial.org/trac/Xerial/wiki/SQLiteJDBC) 
  is included with the SchemaCrawler download. _Please do not replace this bundled driver, or SchemaCrawler will not function._
- The [Oracle](http://www.oracle.com/) [JDBC driver](http://www.oracle.com/technology/software/tech/java/sqlj_jdbc/index.html) 
  needs to be downloaded separately.
- The [Microsoft SQL Server](http://www.microsoft.com/sqlserver/) [jTDS JDBC driver](http://jtds.sourceforge.net/) 
  is included with the SchemaCrawler download.
- The [IBM DB2](http://www.ibm.com/software/data/db2/) [JDBC driver](http://www.ibm.com/software/data/db2/linux-unix-windows/download.html) 
  needs to be downloaded separately.
- The [MySQL](http://www.mysql.com/) [Connector/J JDBC driver](http://dev.mysql.com/downloads/connector/j/) 
  is included with the SchemaCrawler download.
- The [PostgreSQL](http://www.postgresql.org/) [JDBC driver](http://jdbc.postgresql.org/) 
  is included with the SchemaCrawler download.
- The [Sybase IQ](http://www.sybase.com/products/datawarehousing/sybaseiq) [jConnect JDBC driver](http://www.sybase.com/products/allproductsa-z/softwaredeveloperkit/jconnect) needs to be downloaded separately.
- The [Apache Derby](http://db.apache.org/derby/) driver (which can also be used with JavaDB) 
  is included with the SchemaCrawler download.
- Offline database support does not need any JDBC driver, 
  and is included with the SchemaCrawler download.

## How To

SchemaCrawler provides database support in two ways. If you are connecting to a database
not mentioned above, you can simply provide the database connection URL, a username and password.

For the databases mentioned above, you can provide connection details by using the following
command-line options:
- `-server` - identifies the database server, and can be one of `sqlite`, `oracle`, `sqlserver`, 
   `db2`, `mysql`, `postgresql`, `sybaseiq`, `derby`, `offline`
- `-database` - identifies the database, and can have different meaning based on the server type
- `-host` - specifies the database server host; it is optional, and defaults to localhost
- `-port` - specifies the database server port; it is optional, and defaults to the default port for the server type

For example, a typical command-line for SchemaCrawler for Microsoft SQL Server looks like:  
`-server=sqlserver -host=localhost -port=1433 -database=schemacrawler -schemas=schemacrawler.dbo 
-user=schemacrawler -password=schemacrawler`

You should always use the -schemas command-line switch for databases that support it. The value 
for the `-schemas` switch is a regular expression that determines which schemas SchemaCrawler will 
work with. The "schema" is database-dependent - for example, on Microsoft SQL Server, typically 
schemas look like "database_name.user", but for Oracle, typically, schemas look like "USER".

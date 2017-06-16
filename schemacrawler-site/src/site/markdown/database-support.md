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
- The [Microsoft SQL Server](http://www.microsoft.com/sqlserver/) [JDBC driver](https://github.com/Microsoft/mssql-jdbc) 
  is included with the SchemaCrawler download.
- The [IBM DB2](http://www.ibm.com/software/data/db2/) [JDBC driver](http://www.ibm.com/software/data/db2/linux-unix-windows/download.html) 
  needs to be downloaded separately.
- The [MySQL](http://www.mysql.com/) [Connector/J JDBC driver](http://dev.mysql.com/downloads/connector/j/) 
  is included with the SchemaCrawler download.
- The [PostgreSQL](http://www.postgresql.org/) [JDBC driver](http://jdbc.postgresql.org/) 
  is included with the SchemaCrawler download.
- Offline database support does not need any JDBC driver, 
  and is included with the SchemaCrawler download.

## How To

SchemaCrawler provides database support in two ways. If you are connecting to a database
not mentioned above, you can simply provide the database connection URL, a username and password.

For the databases mentioned above, you can provide connection details by using the following
command-line options:

- `-server` - identifies the database server, and can be one of `sqlite`, `oracle`, `sqlserver`, 
   `db2`, `mysql`, `postgresql`, `offline`
- `-database` - identifies the database, and can have different meaning based on the server type
- `-host` - specifies the database server host; it is optional, and defaults to localhost
- `-port` - specifies the database server port; it is optional, and defaults to the default port for the server type

For example, typical command-line options for SchemaCrawler for Microsoft SQL Server looks like:  
`-server=sqlserver -host=localhost -port=1433 -database=schemacrawler -schemas=schemacrawler.dbo 
-user=schemacrawler -password=schemacrawler`

You should always use the -schemas command-line switch for databases that support it. The value 
for the `-schemas` switch is a regular expression that determines which schemas SchemaCrawler will 
work with. The "schema" is database-dependent - for example, on Microsoft SQL Server, typically 
schemas look like "database_name.user", but for Oracle, typically, schemas look like "USER" (in uppercase).

## Making Connections to a Database

### Microsoft SQL Server

You need to specify the host, port, database name, and the schemas you
are interested in, for Microsoft SQL Server.


Typical command-line arguments will look like:
```
-server=sqlserver -host=db.example.com -port=1433 -database=schemacrawler -schemas=schemacrawler.dbo -user=xxxxx -password=xxxxx -infolevel=standard -command=schema
```

If your Microsoft SQL Server instance is set up with Windows authentication or named pipes, you
will need to use a database connection URL. See the 
[documentation for the Microsoft JDBC Driver for SQL Server](https://msdn.microsoft.com/en-us/library/mt720657) 
for details. 

Typical command-line arguments for connecting to SQL Server with Windows authentication will look like:
```
-server=sqlserver -url=<url> -schemas=schemacrawler.dbo -user= -password= -infolevel=standard -command=schema
```

### Oracle

You need to specify the host, port, database name, and the schemas you
are interested in, for Oracle.


Typical command-line arguments will look like:
```
-server=oracle -host=db.example.com -port=1521 -database=ORCL -schemas=SCHEMACRAWLER -user=xxxxx -password=xxxxx -infolevel=standard -command=schema
```

### MySQL

You need to specify the host, port, database name, and the schemas you
are interested in, for MySQL.


Typical command-line arguments will look like:
```
-server=mysql -host=db.example.com -port=3306 -database=schemacrawler -schemas=schemacrawler -user=xxxxx -password=xxxxx -infolevel=standard -command=schema
```

### PostgreSQL

You need to specify the host, port, database name, and the schemas you
are interested in, for PostgreSQL.


Typical command-line arguments will look like:
```
-server=postgresql -host=db.example.com -port=5432 -database=schemacrawler -schemas=public -user=xxxxx -password=xxxxx -infolevel=standard -command=schema
```

### MariaDB

You need to specify the database connection URL, and the schemas you are
interested in, for MariaDB. First make sure that the MariaDB driver is
in the `lib/` folder.


Typical command-line arguments will look like:
```
-url=jdbc:mariadb://scmariadb.cdf972bn8znp.us-east-1.rds.amazonaws.com:3306/schemacrawler -schemas=schemacrawler -user=schemacrawler -password=schemacrawler -tabletypes=UNKNOWN,VIEW -infolevel=standard -command=schema 
```

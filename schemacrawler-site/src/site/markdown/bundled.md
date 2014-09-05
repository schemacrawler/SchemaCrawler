# Database-specific Distributions

## Bundled Distributions

SchemaCrawler supports almost any database that has a JDBC driver, but for
convenience is bundled with drivers for some commonly used RDBMS systems. The
bundled distributions of SchemaCrawler are ready to use for a given RDBMS
system. Most come with open-source JDBC drivers bundled in, so not further
downloads are required. The bundled distributions of SchemaCrawler are:

- [LGPL](http://www.gnu.org/licenses/lgpl.html) [ [download](http://sourceforge.net/projects/schemacrawler/files/SchemaCrawler%20-%20Generic%20Database/) ] 
  SchemaCrawler for any RDBMS system that has a JDBC driver. The JDBC driver needs to be downloaded separately. 
- [LGPL](http://www.gnu.org/licenses/lgpl.html) [ [download](http://sourceforge.net/projects/schemacrawler/files/SchemaCrawler%20-%20IBM%20DB2/) ] 
  SchemaCrawler for [IBM DB2](http://www.ibm.com/software/data/db2/) (but the [IBM DB2 JDBC driver](http://www.ibm.com/software/data/db2/linux-unix-windows/download.html) needs to be downloaded separately) 
- [GPL](http://www.gnu.org/licenses/gpl-3.0.txt)  [ [download](http://sourceforge.net/projects/schemacrawler/files/SchemaCrawler%20-%20Apache%20Derby/) ] 
  SchemaCrawler for [Apache Derby](http://db.apache.org/derby/) (which can also be used with JavaDB) 
- [GPL](http://www.gnu.org/licenses/gpl-3.0.txt)  [ [download](http://sourceforge.net/projects/schemacrawler/files/SchemaCrawler%20-%20Microsoft%20SQL%20Server/) ] 
  SchemaCrawler for [Microsoft SQL Server,](http://www.microsoft.com/sqlserver/) with the [jTDS JDBC driver](http://jtds.sourceforge.net/)
- [GPL](http://www.gnu.org/licenses/gpl-3.0.txt)  [ [download](http://sourceforge.net/projects/schemacrawler/files/SchemaCrawler%20-%20MySQL/) ] 
  SchemaCrawler for [MySQL,](http://www.mysql.com/) with the [MySQL Connector/J JDBC driver](http://dev.mysql.com/downloads/connector/j/)
- [LGPL](http://www.gnu.org/licenses/lgpl.html) [ [download](http://sourceforge.net/projects/schemacrawler/files/SchemaCrawler%20-%20Oracle/) ] 
  SchemaCrawler for [Oracle](http://www.oracle.com/) (but the [Oracle JDBC driver](http://www.oracle.com/technology/software/tech/java/sqlj_jdbc/index.html) needs to be downloaded separately) 
- [GPL](http://www.gnu.org/licenses/gpl-3.0.txt)  [ [download](http://sourceforge.net/projects/schemacrawler/files/SchemaCrawler%20-%20PostgreSQL/) ] 
  SchemaCrawler for [PostgreSQL,](http://www.postgresql.org/) with the [PostgreSQL JDBC driver](http://jdbc.postgresql.org/)
- [GPL](http://www.gnu.org/licenses/gpl-3.0.txt)  [ [download](http://sourceforge.net/projects/schemacrawler/files/SchemaCrawler%20-%20SQLite/) ] 
  SchemaCrawler for [SQLite,](http://www.sqlite.org/) with the [Xerial SQLite JDBC driver](http://www.xerial.org/trac/Xerial/wiki/SQLiteJDBC)
- [LGPL](http://www.gnu.org/licenses/lgpl.html) [ [download](http://sourceforge.net/projects/schemacrawler/files/SchemaCrawler%20-%20Sybase%20IQ/) ] 
  SchemaCrawler for [Sybase IQ](http://www.sybase.com/products/datawarehousing/sybaseiq) (but the [Sybase IQ jConnect JDBC driver](http://www.sybase.com/products/allproductsa-z/softwaredeveloperkit/jconnect) needs to be downloaded separately) 

## How To

Ensure that you have Java 7 or better installed, and download and unzip the
bundled distribution, and you are ready to use SchemaCrawler. No additional
downloads are required. A typical command line for SchemaCrawler for Microsoft
SQL Server looks like:  
`-host=localhost -port=1433 -database=schemacrawler -schemas=schemacrawler.dbo -user=schemacrawler -password=schemacrawler `

The host defaults to localhost, and port defaults to the standard port for the
database system that you are using, so the -host and -port command-line
switches are optional. You should always use the -schemas command-line switch
for databases that support it. The value for the -schemas switch is a regular
expression that determines which schemas SchemaCrawler will work with. The
"schema" is database-dependent - for example, on Microsoft SQL Server,
typically schemas look like "database_name.user", but for Oracle, typically,
schemas look like "USER".

## License

![LGPL](http://www.gnu.org/graphics/lgplv3-88x31.png) 
SchemaCrawler is free, and licensed under the [GNU Lesser General Public License
(LGPL)](http://www.gnu.org/licenses/lgpl-3.0.txt) for distributions that do
not include a JDBC driver.

![GPL](http://www.gnu.org/graphics/gplv3-88x31.png) SchemaCrawler
distributions that are bundled with the JDBC driver are also free, and are
distributed under the [GNU General Public License (GPL)
license.](http://www.gnu.org/licenses/gpl-3.0.txt) The JDBC drivers are
packaged with these SchemaCrawler distributions in their binary form, and
retain their original license.

[Donations](http://sourceforge.net/donate/index.php?group_id=148383) are
welcome.

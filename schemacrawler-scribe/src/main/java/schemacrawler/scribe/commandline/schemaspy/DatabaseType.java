/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.commandline.schemaspy;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * SchemaSpy 6.x database types mapped to SchemaCrawler server identifiers.
 *
 * <p>Types without a SchemaCrawler server mapping are handled in JDBC URL fallback mode.
 */
enum DatabaseType {
  DB2("db2", "db2"),
  DB2NET("db2net", "db2"),
  DB2ZOS("db2zos", "db2"),
  DB2ZOSNET("db2zosnet", "db2"),
  DB2I("db2i", "db2"),
  UDBT4("udbt4", "db2"),
  PGSQL("pgsql", "postgresql"),
  PGSQL11("pgsql11", "postgresql"),
  MYSQL("mysql", "mysql"),
  MYSQL_SOCKET("mysql-socket", "mysql"),
  MARIADB("mariadb", "mysql"),
  MSSQL("mssql", "sqlserver"),
  MSSQL05("mssql05", "sqlserver"),
  MSSQL08("mssql08", "sqlserver"),
  MSSQL17("mssql17", "sqlserver"),
  MSSQL_JTDS("mssql-jtds", "sqlserver"),
  MSSQL_JTDS_INSTANCE("mssql-jtds-instance", "sqlserver"),
  ORA("ora", "oracle"),
  ORATHIN("orathin", "oracle"),
  ORATHIN_SERVICE("orathin-service", "oracle"),
  SQLITE("sqlite", "sqlite"),
  SQLITE_XERIAL("sqlite-xerial", "sqlite"),
  HSQLDB("hsqldb"),
  DERBY("derby"),
  DERBYNET("derbynet"),
  H2("h2"),
  H2_2("h2-2"),
  FIREBIRD("firebird"),
  INFORMIX("informix"),
  SYBASE("sybase"),
  SYBASE2("sybase2"),
  SYBASE3("sybase3"),
  TERADATA("teradata"),
  SNOWFLAKE("snowflake"),
  REDSHIFT("redshift"),
  CLICKHOUSE("clickhouse"),
  IMPALA("impala"),
  HIVE("hive"),
  HIVE_KERBEROS_DRIVERWRAPPER("hive-kerberos-driverwrapper"),
  HIVE_KERBEROS_DRIVERWRAPPER_ZOOKEEPER("hive-kerberos-driverwrapper-zookeeper"),
  NETEZZA("netezza"),
  MAXDB("maxdb"),
  FORCE("force");

  public static Optional<DatabaseType> fromType(final String type) {
    requireNonNull(type, "No SchemaSpy database type provided");
    final String normalizedType = normalize(type);
    return Arrays.stream(values())
        .filter(value -> value.schemaspyType.equals(normalizedType))
        .findFirst();
  }

  public static String supportedDatabaseTypes() {
    return Arrays.stream(values())
        .map(DatabaseType::getSchemaspyType)
        .collect(Collectors.joining(", "));
  }

  private static String normalize(final String type) {
    return type.trim().toLowerCase(Locale.ROOT).replace('_', '-');
  }

  private final String schemaCrawlerServer;
  private final String schemaspyType;

  DatabaseType(final String schemaspyType) {
    this(schemaspyType, null);
  }

  DatabaseType(final String schemaspyType, final String schemaCrawlerServer) {
    this.schemaspyType = requireNonNull(schemaspyType, "No SchemaSpy type provided");
    this.schemaCrawlerServer = schemaCrawlerServer;
  }

  public Optional<String> getSchemaCrawlerServer() {
    return Optional.ofNullable(schemaCrawlerServer);
  }

  public String getSchemaspyType() {
    return schemaspyType;
  }

  public boolean isUrlFallback() {
    return schemaCrawlerServer == null;
  }
}

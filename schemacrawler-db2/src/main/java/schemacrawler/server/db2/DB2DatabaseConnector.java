/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.server.db2;

import java.util.Set;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorOptions;
import schemacrawler.tools.databaseconnector.DatabaseConnectorOptionsBuilder;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.datasource.DatabaseConnectionSourceBuilder;
import us.fatehi.utility.datasource.DatabaseServerType;

public final class DB2DatabaseConnector extends DatabaseConnector {

  private static DatabaseConnectorOptions databaseConnectorOptions() {
    final DatabaseServerType dbServerType = new DatabaseServerType("db2", "IBM DB2");

    final DatabaseConnectionSourceBuilder connectionSourceBuilder =
        DatabaseConnectionSourceBuilder.builder("jdbc:db2://${host}:${port}/${database}")
            .withDefaultPort(50000)
            .withDefaultUrlx("retrieveMessagesFromServerOnGetMessage", true)
            .withAdditionalDriverProperties(
                Set.of(
                    "affinityFailbackInterval",
                    "allowNextOnExhaustedResultSet",
                    "allowNullResultSetForExecuteQuery",
                    "atomicMultiRowInsert",
                    "autocommit",
                    "blockingReadConnectionTimeout",
                    "clientBidiStringType",
                    "clientDebugInfo",
                    "clientRerouteAlternateServerName",
                    "clientRerouteAlternatePortNumber",
                    "clientRerouteServerListJNDIName",
                    "clientRerouteServerListJNDIContext",
                    "commandTimeout",
                    "connectionCloseWithInFlightTransaction",
                    "connectionTimeout",
                    "databaseName",
                    "decimalSeparator",
                    "decimalStringFormat",
                    "defaultIsolationLevel",
                    "deferPrepares",
                    "description",
                    "downgradeHoldCursorsUnderXa",
                    "driverType",
                    "enableClientAffinitiesList",
                    "enableNamedParameterMarkers",
                    "enableBidiLayoutTransformation",
                    "enableSeamlessFailover",
                    "enableSysplexWLB",
                    "fetchSize",
                    "fullyMaterializeLobData",
                    "implicitRollbackOption",
                    "interruptProcessingMode",
                    "keepAliveTimeOut",
                    "loginTimeout",
                    "logWriter",
                    "maxRetriesForClientReroute",
                    "memberConnectTimeout",
                    "progressiveStreaming",
                    "queryCloseImplicit",
                    "queryDataSize",
                    "queryTimeoutInterruptProcessingMode",
                    "receiveBufferSize",
                    "retrieveMessagesFromServerOnGetMessage",
                    "retryIntervalForClientReroute",
                    "securityMechanism",
                    "sendDataAsIs",
                    "serverBidiStringType",
                    "serverName",
                    "sslConnection",
                    "sslTrustStoreLocation",
                    "sslTrustStorePassword",
                    "sslTrustStoreType",
                    "sslKeyStoreLocation",
                    "sslKeyStorePassword",
                    "sslKeyStoreType",
                    "sslVersion",
                    "streamBufferSize",
                    "traceDirectory",
                    "traceFile",
                    "traceFileAppend",
                    "traceLevel",
                    "translateForBitData",
                    "translateBinary",
                    "useCachedCursor",
                    "useJDBC4ColumnNameAndLabelSemantics"));

    final PluginCommand pluginCommand = PluginCommand.newDatabasePluginCommand(dbServerType);
    pluginCommand
        .addOption(
            "server", String.class, "--server=db2%n" + "Loads SchemaCrawler plug-in for IBM DB2")
        .addOption("host", String.class, "Host name%n" + "Optional, defaults to localhost")
        .addOption("port", Integer.class, "Port number%n" + "Optional, defaults to 50000")
        .addOption("database", String.class, "Database name");

    return DatabaseConnectorOptionsBuilder.builder(dbServerType)
        .withHelpCommand(pluginCommand)
        .withUrlStartsWith("jdbc:db2:")
        .withInformationSchemaViewsFromResourceFolder("/db2.information_schema")
        .withDatabaseConnectionSourceBuilder(() -> connectionSourceBuilder)
        .build();
  }

  public DB2DatabaseConnector() {
    super(databaseConnectorOptions());
  }
}

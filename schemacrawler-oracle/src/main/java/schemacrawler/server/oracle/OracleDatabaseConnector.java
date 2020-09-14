/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.server.oracle;


import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.data_dictionary_all;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.foreignKeysRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.functionParametersRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.functionsRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.indexesRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.primaryKeysRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.procedureParametersRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.proceduresRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableColumnsRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tablesRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.typeInfoRetrievalStrategy;
import java.io.IOException;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;

public final class OracleDatabaseConnector
  extends DatabaseConnector
{

  static final DatabaseServerType DB_SERVER_TYPE =
    new DatabaseServerType("oracle", "Oracle");


  public OracleDatabaseConnector() throws IOException
  {
    super(DB_SERVER_TYPE, 
        url -> url != null && url.startsWith("jdbc:oracle:"),
        new OracleInformationSchemaViewsBuilder(),        
        (schemaRetrievalOptionsBuilder,
            connection) -> schemaRetrievalOptionsBuilder
                .with(typeInfoRetrievalStrategy, data_dictionary_all)
                .with(tablesRetrievalStrategy, data_dictionary_all)
                .with(tableColumnsRetrievalStrategy, data_dictionary_all)
                .with(primaryKeysRetrievalStrategy, data_dictionary_all)
                .with(foreignKeysRetrievalStrategy, data_dictionary_all)
                .with(indexesRetrievalStrategy, data_dictionary_all)
                .with(proceduresRetrievalStrategy, data_dictionary_all)
                .with(procedureParametersRetrievalStrategy, data_dictionary_all)
                .with(functionsRetrievalStrategy, data_dictionary_all)
                .with(functionParametersRetrievalStrategy, data_dictionary_all),
        (limitOptionsBuilder, connection) -> limitOptionsBuilder
            .includeSchemas(new RegularExpressionExclusionRule(
                "ANONYMOUS|APEX_PUBLIC_USER|APPQOSSYS|BI|CTXSYS|DBSNMP|DIP|EXFSYS|FLOWS_30000|FLOWS_FILES|GSMADMIN_INTERNAL|HR|IX|LBACSYS|MDDATA|MDSYS|MGMT_VIEW|OE|OLAPSYS|ORACLE_OCM|ORDPLUGINS|ORDSYS|OUTLN|OWBSYS|PM|RDSADMIN|SCOTT|SH|SI_INFORMTN_SCHEMA|SPATIAL_CSW_ADMIN_USR|SPATIAL_WFS_ADMIN_USR|SYS|SYSMAN|\\\"SYSTEM\\\"|TSMSYS|WKPROXY|WKSYS|WK_TEST|WMSYS|XDB|APEX_[0-9]{6}|FLOWS_[0-9]{5,6}|XS\\$NULL")),
        new OracleUrlBuilder());

    System.setProperty("oracle.jdbc.Trace", "true");
  }

  @Override
  public PluginCommand getHelpCommand()
  {
    final PluginCommand pluginCommand = super.getHelpCommand();
    pluginCommand
      .addOption("server",
                 "--server=oracle%n" + "Loads SchemaCrawler plug-in for Oracle",
                 String.class)
      .addOption("host",
                 "Host name%n" + "Optional, defaults to localhost",
                 String.class)
      .addOption("port",
                 "Port number%n" + "Optional, defaults to 1521",
                 Integer.class)
      .addOption("database",
                 "Oracle Service Name%n"
                 + "You can use a query similar to the one below to find it.%n"
                 + "SELECT GLOBAL_NAME FROM GLOBAL_NAME",
                 String.class);
    return pluginCommand;
  }

}

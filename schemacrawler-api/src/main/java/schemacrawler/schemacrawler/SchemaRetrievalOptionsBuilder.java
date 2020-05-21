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
package schemacrawler.schemacrawler;


import static schemacrawler.plugin.EnumDataTypeHelper.NO_OP_ENUM_DATA_TYPE_HELPER;
import static sf.util.Utility.isBlank;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

import schemacrawler.plugin.EnumDataTypeHelper;
import schemacrawler.utility.TypeMap;

public final class SchemaRetrievalOptionsBuilder
  implements OptionsBuilder<SchemaRetrievalOptionsBuilder, SchemaRetrievalOptions>
{

  private static final String prefix =
    "schemacrawler.schema.retrieval.strategy";

  private static final String SC_RETRIEVAL_TABLES = prefix + ".tables";
  private static final String SC_RETRIEVAL_TABLE_COLUMNS =
    prefix + ".tablecolumns";

  private static final String SC_RETRIEVAL_PRIMARY_KEYS =
    prefix + ".primarykeys";
  private static final String SC_RETRIEVAL_INDEXES = prefix + ".indexes";
  private static final String SC_RETRIEVAL_FOREIGN_KEYS =
    prefix + ".foreignkeys";

  private static final String SC_RETRIEVAL_PROCEDURES = prefix + ".procedures";
  private static final String SC_RETRIEVAL_PROCEDURE_COLUMNS =
    prefix + ".procedurecolumns";
  private static final String SC_RETRIEVAL_FUNCTIONS = prefix + ".functions";
  private static final String SC_RETRIEVAL_FUNCTION_COLUMNS =
    prefix + ".functioncolumns";

  public static SchemaRetrievalOptionsBuilder builder()
  {
    return new SchemaRetrievalOptionsBuilder();
  }

  public static SchemaRetrievalOptionsBuilder builder(final SchemaRetrievalOptions options)
  {
    return new SchemaRetrievalOptionsBuilder().fromOptions(options);
  }

  public static SchemaRetrievalOptions newSchemaRetrievalOptions()
  {
    return new SchemaRetrievalOptionsBuilder().toOptions();
  }

  public static SchemaRetrievalOptions newSchemaRetrievalOptions(final Config config)
  {
    return new SchemaRetrievalOptionsBuilder()
      .fromConfig(config)
      .toOptions();
  }

  private DatabaseServerType dbServerType;
  private MetadataRetrievalStrategy fkRetrievalStrategy;
  private MetadataRetrievalStrategy functionColumnRetrievalStrategy;
  private MetadataRetrievalStrategy functionRetrievalStrategy;
  private String identifierQuoteString;
  private Identifiers identifiers;
  private MetadataRetrievalStrategy indexRetrievalStrategy;
  private InformationSchemaViews informationSchemaViews;
  private Optional<Boolean> overridesSupportSchemas;
  private Optional<Boolean> overridesSupportsCatalogs;
  private Optional<TypeMap> overridesTypeMap;
  private MetadataRetrievalStrategy pkRetrievalStrategy;
  private MetadataRetrievalStrategy procedureColumnRetrievalStrategy;
  private MetadataRetrievalStrategy procedureRetrievalStrategy;
  private boolean supportsCatalogs;
  private boolean supportsSchemas;
  private MetadataRetrievalStrategy tableColumnRetrievalStrategy;
  private MetadataRetrievalStrategy tableRetrievalStrategy;
  private EnumDataTypeHelper enumDataTypeHelper;

  private SchemaRetrievalOptionsBuilder()
  {
    dbServerType = DatabaseServerType.UNKNOWN;
    informationSchemaViews = InformationSchemaViewsBuilder.newInformationSchemaViews();
    overridesSupportSchemas = Optional.empty();
    overridesSupportsCatalogs = Optional.empty();
    supportsCatalogs = true;
    supportsSchemas = true;
    identifierQuoteString = "";
    identifiers = Identifiers.STANDARD;
    tableRetrievalStrategy = MetadataRetrievalStrategy.metadata;
    tableColumnRetrievalStrategy = MetadataRetrievalStrategy.metadata;
    pkRetrievalStrategy = MetadataRetrievalStrategy.metadata;
    indexRetrievalStrategy = MetadataRetrievalStrategy.metadata;
    fkRetrievalStrategy = MetadataRetrievalStrategy.metadata;
    procedureRetrievalStrategy = MetadataRetrievalStrategy.metadata;
    procedureColumnRetrievalStrategy = MetadataRetrievalStrategy.metadata;
    functionRetrievalStrategy = MetadataRetrievalStrategy.metadata;
    functionColumnRetrievalStrategy = MetadataRetrievalStrategy.metadata;
    overridesTypeMap = Optional.empty();
    enumDataTypeHelper = NO_OP_ENUM_DATA_TYPE_HELPER;
  }

  @Override
  public SchemaRetrievalOptionsBuilder fromConfig(final Config config)
  {
    final Config configProperties;
    if (config == null)
    {
      configProperties = new Config();
    }
    else
    {
      configProperties = new Config(config);
    }

    informationSchemaViews = InformationSchemaViewsBuilder
      .builder(informationSchemaViews)
      .fromConfig(configProperties)
      .toOptions();

    tableRetrievalStrategy = configProperties.getEnumValue(SC_RETRIEVAL_TABLES,
                                                           tableRetrievalStrategy);
    tableColumnRetrievalStrategy = configProperties.getEnumValue(
      SC_RETRIEVAL_TABLE_COLUMNS,
      tableColumnRetrievalStrategy);
    pkRetrievalStrategy = configProperties.getEnumValue(
      SC_RETRIEVAL_PRIMARY_KEYS,
      pkRetrievalStrategy);
    indexRetrievalStrategy = configProperties.getEnumValue(SC_RETRIEVAL_INDEXES,
                                                           indexRetrievalStrategy);
    fkRetrievalStrategy = configProperties.getEnumValue(
      SC_RETRIEVAL_FOREIGN_KEYS,
      fkRetrievalStrategy);
    procedureRetrievalStrategy = configProperties.getEnumValue(
      SC_RETRIEVAL_PROCEDURES,
      procedureRetrievalStrategy);
    procedureColumnRetrievalStrategy = configProperties.getEnumValue(
      SC_RETRIEVAL_PROCEDURE_COLUMNS,
      procedureColumnRetrievalStrategy);
    functionRetrievalStrategy = configProperties.getEnumValue(
      SC_RETRIEVAL_FUNCTIONS,
      functionRetrievalStrategy);
    functionColumnRetrievalStrategy = configProperties.getEnumValue(
      SC_RETRIEVAL_FUNCTION_COLUMNS,
      functionColumnRetrievalStrategy);

    return this;
  }

  @Override
  public SchemaRetrievalOptionsBuilder fromOptions(final SchemaRetrievalOptions options)
  {
    if (options == null)
    {
      return this;
    }

    dbServerType = options.getDatabaseServerType();
    informationSchemaViews = options.getInformationSchemaViews();
    overridesSupportSchemas = Optional.empty();
    overridesSupportsCatalogs = Optional.empty();
    supportsCatalogs = options.isSupportsCatalogs();
    supportsSchemas = options.isSupportsSchemas();
    identifierQuoteString = options.getIdentifierQuoteString();
    identifiers = options.getIdentifiers();
    tableRetrievalStrategy = options.getTableRetrievalStrategy();
    tableColumnRetrievalStrategy = options.getTableColumnRetrievalStrategy();
    pkRetrievalStrategy = options.getPrimaryKeyRetrievalStrategy();
    indexRetrievalStrategy = options.getIndexRetrievalStrategy();
    fkRetrievalStrategy = options.getForeignKeyRetrievalStrategy();
    procedureRetrievalStrategy = options.getProcedureRetrievalStrategy();
    procedureColumnRetrievalStrategy =
      options.getProcedureColumnRetrievalStrategy();
    functionRetrievalStrategy = options.getFunctionRetrievalStrategy();
    functionColumnRetrievalStrategy =
      options.getFunctionColumnRetrievalStrategy();
    overridesTypeMap = Optional.empty();

    return this;
  }

  @Override
  public Config toConfig()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public SchemaRetrievalOptions toOptions()
  {
    return new SchemaRetrievalOptions(this);
  }

  public SchemaRetrievalOptionsBuilder fromConnnection(final Connection connection)
  {
    if (connection == null)
    {
      return this;
    }

    DatabaseMetaData metaData;
    try
    {
      metaData = connection.getMetaData();
    }
    catch (final SQLException e)
    {
      // Ignore
      metaData = null;
    }

    identifierQuoteString = lookupIdentifierQuoteString(metaData);
    identifiers = Identifiers
      .identifiers()
      .withConnectionIfPossible(connection)
      .withIdentifierQuoteString(identifierQuoteString)
      .build();

    supportsCatalogs = lookupSupportsCatalogs(metaData);
    supportsSchemas = lookupSupportsSchemas(metaData);

    if (!overridesTypeMap.isPresent())
    {
      overridesTypeMap = Optional.of(new TypeMap(connection));
    }

    return this;
  }

  public DatabaseServerType getDatabaseServerType()
  {
    return dbServerType;
  }

  public MetadataRetrievalStrategy getForeignKeyRetrievalStrategy()
  {
    return fkRetrievalStrategy;
  }

  public MetadataRetrievalStrategy getFunctionColumnRetrievalStrategy()
  {
    return functionColumnRetrievalStrategy;
  }

  public MetadataRetrievalStrategy getFunctionRetrievalStrategy()
  {
    return functionRetrievalStrategy;
  }

  public String getIdentifierQuoteString()
  {
    return identifierQuoteString;
  }

  public Identifiers getIdentifiers()
  {
    return identifiers;
  }

  public MetadataRetrievalStrategy getIndexRetrievalStrategy()
  {
    return indexRetrievalStrategy;
  }

  public InformationSchemaViews getInformationSchemaViews()
  {
    return informationSchemaViews;
  }

  public MetadataRetrievalStrategy getPrimaryKeyRetrievalStrategy()
  {
    return pkRetrievalStrategy;
  }

  public MetadataRetrievalStrategy getProcedureColumnRetrievalStrategy()
  {
    return procedureColumnRetrievalStrategy;
  }

  public MetadataRetrievalStrategy getProcedureRetrievalStrategy()
  {
    return procedureRetrievalStrategy;
  }

  public MetadataRetrievalStrategy getTableColumnRetrievalStrategy()
  {
    return tableColumnRetrievalStrategy;
  }

  public MetadataRetrievalStrategy getTableRetrievalStrategy()
  {
    return tableRetrievalStrategy;
  }

  public TypeMap getTypeMap()
  {
    return overridesTypeMap.orElse(new TypeMap());
  }

  public boolean isSupportsCatalogs()
  {
    return supportsCatalogs;
  }

  public boolean isSupportsSchemas()
  {
    return supportsSchemas;
  }

  public SchemaRetrievalOptionsBuilder withDatabaseServerType(final DatabaseServerType dbServerType)
  {
    if (dbServerType == null)
    {
      this.dbServerType = DatabaseServerType.UNKNOWN;
    }
    else
    {
      this.dbServerType = dbServerType;
    }
    return this;
  }

  /**
   * Overrides the JDBC driver provided information about whether the database
   * supports catalogs.
   */
  public SchemaRetrievalOptionsBuilder withDoesNotSupportCatalogs()
  {
    overridesSupportsCatalogs = Optional.of(false);
    return this;
  }

  /**
   * Overrides the JDBC driver provided information about whether the database
   * supports schema.
   */
  public SchemaRetrievalOptionsBuilder withDoesNotSupportSchemas()
  {
    overridesSupportSchemas = Optional.of(false);
    return this;
  }

  public SchemaRetrievalOptionsBuilder withForeignKeyRetrievalStrategy(final MetadataRetrievalStrategy fkRetrievalStrategy)
  {
    if (fkRetrievalStrategy == null)
    {
      this.fkRetrievalStrategy = MetadataRetrievalStrategy.metadata;
    }
    else
    {
      this.fkRetrievalStrategy = fkRetrievalStrategy;
    }
    return this;
  }

  public SchemaRetrievalOptionsBuilder withFunctionColumnRetrievalStrategy(final MetadataRetrievalStrategy functionColumnRetrievalStrategy)
  {
    if (functionColumnRetrievalStrategy == null)
    {
      this.functionColumnRetrievalStrategy = MetadataRetrievalStrategy.metadata;
    }
    else
    {
      this.functionColumnRetrievalStrategy = functionColumnRetrievalStrategy;
    }
    return this;
  }

  public SchemaRetrievalOptionsBuilder withFunctionRetrievalStrategy(final MetadataRetrievalStrategy functionRetrievalStrategy)
  {
    if (functionRetrievalStrategy == null)
    {
      this.functionRetrievalStrategy = MetadataRetrievalStrategy.metadata;
    }
    else
    {
      this.functionRetrievalStrategy = functionRetrievalStrategy;
    }
    return this;
  }

  /**
   * Overrides the JDBC driver provided information about the identifier quote
   * string.
   *
   * @param identifierQuoteString
   *   Value for the override
   */
  public SchemaRetrievalOptionsBuilder withIdentifierQuoteString(final String identifierQuoteString)
  {
    if (isBlank(identifierQuoteString))
    {
      this.identifierQuoteString = "";
    }
    else
    {
      this.identifierQuoteString = identifierQuoteString;
    }
    return this;
  }

  public SchemaRetrievalOptionsBuilder withIndexRetrievalStrategy(final MetadataRetrievalStrategy indexRetrievalStrategy)
  {
    if (indexRetrievalStrategy == null)
    {
      this.indexRetrievalStrategy = MetadataRetrievalStrategy.metadata;
    }
    else
    {
      this.indexRetrievalStrategy = indexRetrievalStrategy;
    }
    return this;
  }

  public SchemaRetrievalOptionsBuilder withInformationSchemaViews(final InformationSchemaViews informationSchemaViews)
  {
    this.informationSchemaViews = InformationSchemaViewsBuilder
      .builder()
      .fromOptions(informationSchemaViews)
      .toOptions();
    return this;
  }

  public SchemaRetrievalOptionsBuilder withInformationSchemaViews(final Map<String, String> informationSchemaViewsMap)
  {
    this.informationSchemaViews = InformationSchemaViewsBuilder
      .builder(informationSchemaViews)
      .fromConfig(new Config(informationSchemaViewsMap))
      .toOptions();
    return this;
  }

  public SchemaRetrievalOptionsBuilder withoutIdentifierQuoteString()
  {
    identifierQuoteString = "";
    return this;
  }

  public SchemaRetrievalOptionsBuilder withoutSupportsCatalogs()
  {
    overridesSupportsCatalogs = Optional.empty();
    return this;
  }

  public SchemaRetrievalOptionsBuilder withoutSupportsSchemas()
  {
    overridesSupportSchemas = Optional.empty();
    return this;
  }

  public SchemaRetrievalOptionsBuilder withPrimaryKeyRetrievalStrategy(final MetadataRetrievalStrategy pkRetrievalStrategy)
  {
    if (pkRetrievalStrategy == null)
    {
      this.pkRetrievalStrategy = MetadataRetrievalStrategy.metadata;
    }
    else
    {
      this.pkRetrievalStrategy = pkRetrievalStrategy;
    }
    return this;
  }

  public SchemaRetrievalOptionsBuilder withProcedureColumnRetrievalStrategy(
    final MetadataRetrievalStrategy procedureColumnRetrievalStrategy)
  {
    if (procedureColumnRetrievalStrategy == null)
    {
      this.procedureColumnRetrievalStrategy =
        MetadataRetrievalStrategy.metadata;
    }
    else
    {
      this.procedureColumnRetrievalStrategy = procedureColumnRetrievalStrategy;
    }
    return this;
  }

  public SchemaRetrievalOptionsBuilder withProcedureRetrievalStrategy(final MetadataRetrievalStrategy procedureRetrievalStrategy)
  {
    if (procedureRetrievalStrategy == null)
    {
      this.procedureRetrievalStrategy = MetadataRetrievalStrategy.metadata;
    }
    else
    {
      this.procedureRetrievalStrategy = procedureRetrievalStrategy;
    }
    return this;
  }

  /**
   * Overrides the JDBC driver provided information about whether the database
   * supports catalogs.
   */
  public SchemaRetrievalOptionsBuilder withSupportsCatalogs()
  {
    overridesSupportsCatalogs = Optional.of(true);
    return this;
  }

  /**
   * Overrides the JDBC driver provided information about whether the database
   * supports schema.
   */
  public SchemaRetrievalOptionsBuilder withSupportsSchemas()
  {
    overridesSupportSchemas = Optional.of(true);
    return this;
  }

  public SchemaRetrievalOptionsBuilder withTableColumnRetrievalStrategy(final MetadataRetrievalStrategy tableColumnRetrievalStrategy)
  {
    if (tableColumnRetrievalStrategy == null)
    {
      this.tableColumnRetrievalStrategy = MetadataRetrievalStrategy.metadata;
    }
    else
    {
      this.tableColumnRetrievalStrategy = tableColumnRetrievalStrategy;
    }
    return this;
  }

  public SchemaRetrievalOptionsBuilder withTableRetrievalStrategy(final MetadataRetrievalStrategy tableRetrievalStrategy)
  {
    if (tableRetrievalStrategy == null)
    {
      this.tableRetrievalStrategy = MetadataRetrievalStrategy.metadata;
    }
    else
    {
      this.tableRetrievalStrategy = tableRetrievalStrategy;
    }
    return this;
  }

  public SchemaRetrievalOptionsBuilder withTypeMap(final Map<String, Class<?>> typeMap)
  {
    if (typeMap == null)
    {
      overridesTypeMap = Optional.empty();
    }
    else
    {
      overridesTypeMap = Optional.of(new TypeMap(typeMap));
    }
    return this;
  }

  public SchemaRetrievalOptionsBuilder withEnumDataTypeHelper(final EnumDataTypeHelper enumDataTypeHelper)
  {
    if (enumDataTypeHelper != null) {
      this.enumDataTypeHelper = enumDataTypeHelper;
    } else {
      this.enumDataTypeHelper = NO_OP_ENUM_DATA_TYPE_HELPER;
    }

    return this;
  }

  public EnumDataTypeHelper getEnumDataTypeHelper()
  {
    return enumDataTypeHelper;
  }

  private String lookupIdentifierQuoteString(final DatabaseMetaData metaData)
  {
    // Default to SQL standard default
    String identifierQuoteString = "\"";

    if (!isBlank(this.identifierQuoteString))
    {
      identifierQuoteString = this.identifierQuoteString;
    }
    else if (metaData != null)
    {
      try
      {
        identifierQuoteString = metaData.getIdentifierQuoteString();
      }
      catch (final SQLException e)
      {
        // Ignore
      }
    }

    if (isBlank(identifierQuoteString))
    {
      identifierQuoteString = "";
    }

    return identifierQuoteString;
  }

  private boolean lookupSupportsCatalogs(final DatabaseMetaData metaData)
  {
    boolean supportsCatalogs = true;
    if (overridesSupportsCatalogs.isPresent())
    {
      supportsCatalogs = overridesSupportsCatalogs.get();
    }
    else if (metaData != null)
    {
      try
      {
        supportsCatalogs = metaData.supportsCatalogsInTableDefinitions();
      }
      catch (final SQLException e)
      {
        // Ignore
      }
    }
    return supportsCatalogs;
  }

  private boolean lookupSupportsSchemas(final DatabaseMetaData metaData)
  {
    boolean supportsSchemas = true;
    if (overridesSupportSchemas.isPresent())
    {
      supportsSchemas = overridesSupportSchemas.get();
    }
    else if (metaData != null)
    {
      try
      {
        supportsSchemas = metaData.supportsSchemasInTableDefinitions();
      }
      catch (final SQLException e)
      {
        // Ignore
      }
    }

    return supportsSchemas;
  }

}

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


import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.foreignKeysRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.functionParametersRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.functionsRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.indexesRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.primaryKeysRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.procedureParametersRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableColumnsRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tablesRetrievalStrategy;
import static sf.util.Utility.isBlank;

import java.util.EnumMap;
import java.util.Map;

import schemacrawler.plugin.EnumDataTypeHelper;
import schemacrawler.utility.TypeMap;
import sf.util.ObjectToString;

/**
 * Provides for database specific overrides for SchemaCrawler functionality. This can add or inject database plugins, or
 * override defaults. It is recommended to build these options using factory methods in SchemaCrawlerUtility.
 *
 * @author Sualeh Fatehi <sualeh@hotmail.com>
 */
public final class SchemaRetrievalOptions
  implements Options
{

  private final DatabaseServerType dbServerType;
  private final String identifierQuoteString;
  private final Identifiers identifiers;
  private final InformationSchemaViews informationSchemaViews;
  private final boolean supportsCatalogs;
  private final boolean supportsSchemas;
  private final TypeMap typeMap;
  private final EnumDataTypeHelper enumDataTypeHelper;
  EnumMap<SchemaInfoMetadataRetrievalStrategy, MetadataRetrievalStrategy> metadataRetrievalStrategyMap;

  protected SchemaRetrievalOptions(final SchemaRetrievalOptionsBuilder builder)
  {
    final SchemaRetrievalOptionsBuilder bldr = builder == null? SchemaRetrievalOptionsBuilder.builder(): builder;
    dbServerType = bldr.dbServerType;
    supportsSchemas = bldr.supportsSchemas;
    supportsCatalogs = bldr.supportsCatalogs;
    identifierQuoteString = bldr.identifierQuoteString;
    informationSchemaViews = bldr.informationSchemaViews;
    identifiers = bldr.identifiers;
    typeMap = bldr.overridesTypeMap.orElse(new TypeMap());
    enumDataTypeHelper = bldr.enumDataTypeHelper;
    metadataRetrievalStrategyMap = new EnumMap<>(bldr.metadataRetrievalStrategyMap);
  }

  public EnumDataTypeHelper getEnumDataTypeHelper()
  {
    return enumDataTypeHelper;
  }

  public DatabaseServerType getDatabaseServerType()
  {
    return dbServerType;
  }

  public MetadataRetrievalStrategy getForeignKeyRetrievalStrategy()
  {
    return metadataRetrievalStrategyMap.get(foreignKeysRetrievalStrategy);
  }

  public MetadataRetrievalStrategy getFunctionColumnRetrievalStrategy()
  {
    return metadataRetrievalStrategyMap.get(functionParametersRetrievalStrategy);
  }

  public MetadataRetrievalStrategy getFunctionRetrievalStrategy()
  {
    return metadataRetrievalStrategyMap.get(functionsRetrievalStrategy);
  }

  public String getIdentifierQuoteString()
  {
    if (!hasOverrideForIdentifierQuoteString())
    {
      return "";
    }
    return identifierQuoteString;
  }

  public Identifiers getIdentifiers()
  {
    return identifiers;
  }

  public MetadataRetrievalStrategy getIndexRetrievalStrategy()
  {
    return metadataRetrievalStrategyMap.get(indexesRetrievalStrategy);
  }

  public InformationSchemaViews getInformationSchemaViews()
  {
    return informationSchemaViews;
  }

  public MetadataRetrievalStrategy getPrimaryKeyRetrievalStrategy()
  {
    return metadataRetrievalStrategyMap.get(primaryKeysRetrievalStrategy);
  }

  public MetadataRetrievalStrategy getProcedureColumnRetrievalStrategy()
  {
    return metadataRetrievalStrategyMap.get(procedureParametersRetrievalStrategy);
  }

  public MetadataRetrievalStrategy getProcedureRetrievalStrategy()
  {
    return metadataRetrievalStrategyMap.get(procedureParametersRetrievalStrategy);
  }

  public MetadataRetrievalStrategy getTableColumnRetrievalStrategy()
  {
    return metadataRetrievalStrategyMap.get(tableColumnsRetrievalStrategy);
  }

  public MetadataRetrievalStrategy getTableRetrievalStrategy()
  {
    return metadataRetrievalStrategyMap.get(tablesRetrievalStrategy);
  }

  public TypeMap getTypeMap()
  {
    return typeMap;
  }

  public boolean hasOverrideForIdentifierQuoteString()
  {
    return !isBlank(identifierQuoteString);
  }

  public boolean hasOverrideForTypeMap()
  {
    return typeMap != null;
  }

  public boolean isSupportsCatalogs()
  {
    return supportsCatalogs;
  }

  public boolean isSupportsSchemas()
  {
    return supportsSchemas;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return ObjectToString.toString(this);
  }

  Map<SchemaInfoMetadataRetrievalStrategy, MetadataRetrievalStrategy> getMetadataRetrievalStrategyMap()
  {
    return new EnumMap<>(metadataRetrievalStrategyMap);
  }

}

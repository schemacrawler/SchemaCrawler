/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static sf.util.Utility.isBlank;

import java.util.Optional;

import schemacrawler.crawl.MetadataRetrievalStrategy;

public final class DatabaseSpecificOverrideOptions
  implements Options
{

  private static final long serialVersionUID = -5593417085363698921L;

  private final Optional<Boolean> supportsSchemas;
  private final Optional<Boolean> supportsCatalogs;
  private final MetadataRetrievalStrategy tableRetrievalStrategy;
  private final MetadataRetrievalStrategy tableColumnRetrievalStrategy;
  private final MetadataRetrievalStrategy fkRetrievalStrategy;
  private final String identifierQuoteString;
  private final InformationSchemaViews informationSchemaViews;

  public DatabaseSpecificOverrideOptions()
  {
    this(null);
  }

  protected DatabaseSpecificOverrideOptions(final DatabaseSpecificOverrideOptionsBuilder builder)
  {
    final DatabaseSpecificOverrideOptionsBuilder bldr = builder == null? new DatabaseSpecificOverrideOptionsBuilder()
                                                                       : builder;
    supportsSchemas = bldr.getSupportsSchemas();
    supportsCatalogs = bldr.getSupportsCatalogs();
    tableRetrievalStrategy = bldr.getTableRetrievalStrategy();
    tableColumnRetrievalStrategy = bldr.getTableColumnRetrievalStrategy();
    fkRetrievalStrategy = bldr.getForeignKeyRetrievalStrategy();
    identifierQuoteString = bldr.getIdentifierQuoteString();
    informationSchemaViews = bldr.getInformationSchemaViewsBuilder()
      .toOptions();
  }

  public MetadataRetrievalStrategy getForeignKeyRetrievalStrategy()
  {
    return fkRetrievalStrategy;
  }

  public String getIdentifierQuoteString()
  {
    if (!hasOverrideForIdentifierQuoteString())
    {
      return "";
    }
    return identifierQuoteString;
  }

  public InformationSchemaViews getInformationSchemaViews()
  {
    return informationSchemaViews;
  }

  public MetadataRetrievalStrategy getTableColumnRetrievalStrategy()
  {
    return tableColumnRetrievalStrategy;
  }

  public MetadataRetrievalStrategy getTableRetrievalStrategy()
  {
    return tableRetrievalStrategy;
  }

  public boolean hasOverrideForIdentifierQuoteString()
  {
    return !isBlank(identifierQuoteString);
  }

  public boolean hasOverrideForSupportsCatalogs()
  {
    return supportsCatalogs.isPresent();
  }

  public boolean hasOverrideForSupportsSchemas()
  {
    return supportsSchemas.isPresent();
  }

  public boolean isSupportsCatalogs()
  {
    return supportsCatalogs.orElse(true);
  }

  public boolean isSupportsSchemas()
  {
    return supportsSchemas.orElse(true);
  }

}

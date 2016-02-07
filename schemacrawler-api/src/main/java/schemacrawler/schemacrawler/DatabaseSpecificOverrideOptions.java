package schemacrawler.schemacrawler;


import static sf.util.Utility.isBlank;

import java.util.Optional;

import schemacrawler.crawl.TableColumnRetrievalStrategy;

public final class DatabaseSpecificOverrideOptions
  implements Options
{

  private static final long serialVersionUID = -5593417085363698921L;

  private final Optional<Boolean> supportsSchemas;
  private final Optional<Boolean> supportsCatalogs;
  private final TableColumnRetrievalStrategy tableColumnRetrievalStrategy;
  private final String identifierQuoteString;
  private final InformationSchemaViews informationSchemaViews;

  public DatabaseSpecificOverrideOptions()
  {
    this(null);
  }

  protected DatabaseSpecificOverrideOptions(final DatabaseSpecificOverrideOptionsBuilder builder)
  {
    if (builder == null)
    {
      supportsSchemas = Optional.empty();
      supportsCatalogs = Optional.empty();
      tableColumnRetrievalStrategy = TableColumnRetrievalStrategy.metadata_each_table;
      identifierQuoteString = "";
      informationSchemaViews = new InformationSchemaViews();
    }
    else
    {
      supportsSchemas = builder.getSupportsSchemas();
      supportsCatalogs = builder.getSupportsCatalogs();
      tableColumnRetrievalStrategy = builder.getTableColumnRetrievalStrategy();
      identifierQuoteString = builder.getIdentifierQuoteString();
      informationSchemaViews = builder.getInformationSchemaViewsBuilder()
        .toOptions();
    }

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

  public TableColumnRetrievalStrategy getTableColumnRetrievalStrategy()
  {
    return tableColumnRetrievalStrategy;
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

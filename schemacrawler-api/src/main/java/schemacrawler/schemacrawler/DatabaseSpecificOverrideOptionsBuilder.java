package schemacrawler.schemacrawler;


import java.util.Map;
import java.util.Optional;

import schemacrawler.crawl.MetadataRetrievalStrategy;

public class DatabaseSpecificOverrideOptionsBuilder
  implements OptionsBuilder<DatabaseSpecificOverrideOptions>
{

  private Optional<Boolean> supportsSchemas;
  private Optional<Boolean> supportsCatalogs;
  private MetadataRetrievalStrategy tableColumnRetrievalStrategy;
  private String identifierQuoteString;
  private final InformationSchemaViewsBuilder informationSchemaViewsBuilder;

  public DatabaseSpecificOverrideOptionsBuilder()
  {
    informationSchemaViewsBuilder = new InformationSchemaViewsBuilder();
    supportsSchemas = Optional.empty();
    supportsCatalogs = Optional.empty();
    identifierQuoteString = "";
    tableColumnRetrievalStrategy = MetadataRetrievalStrategy.metadata;
  }

  public DatabaseSpecificOverrideOptionsBuilder(final Map<String, String> map)
  {
    this();
    fromConfig(map);
  }

  /**
   * Overrides the JDBC driver provided information about whether the
   * database supports catalogs.
   */
  public DatabaseSpecificOverrideOptionsBuilder doesNotSupportCatalogs()
  {
    supportsCatalogs = Optional.of(false);
    return this;
  }

  /**
   * Overrides the JDBC driver provided information about whether the
   * database supports schema.
   */
  public DatabaseSpecificOverrideOptionsBuilder doesNotSupportSchemas()
  {
    supportsSchemas = Optional.of(false);
    return this;
  }

  @Override
  public DatabaseSpecificOverrideOptionsBuilder fromConfig(final Map<String, String> map)
  {
    informationSchemaViewsBuilder.fromConfig(map);
    return this;
  }

  public String getIdentifierQuoteString()
  {
    return identifierQuoteString;
  }

  public InformationSchemaViewsBuilder getInformationSchemaViewsBuilder()
  {
    return informationSchemaViewsBuilder;
  }

  public Optional<Boolean> getSupportsCatalogs()
  {
    return supportsCatalogs;
  }

  public Optional<Boolean> getSupportsSchemas()
  {
    return supportsSchemas;
  }

  public MetadataRetrievalStrategy getTableColumnRetrievalStrategy()
  {
    return tableColumnRetrievalStrategy;
  }

  /**
   * Overrides the JDBC driver provided information about the identifier
   * quote string.
   *
   * @param getIdentifierQuoteString
   *        Value for the override
   */
  public DatabaseSpecificOverrideOptionsBuilder identifierQuoteString(final String identifierQuoteString)
  {
    this.identifierQuoteString = identifierQuoteString;
    return this;
  }

  /**
   * Overrides the JDBC driver provided information about whether the
   * database supports catalogs.
   */
  public DatabaseSpecificOverrideOptionsBuilder supportsCatalogs()
  {
    supportsCatalogs = Optional.of(true);
    return this;
  }

  /**
   * Overrides the JDBC driver provided information about whether the
   * database supports schema.
   */
  public DatabaseSpecificOverrideOptionsBuilder supportsSchemas()
  {
    supportsSchemas = Optional.of(true);
    return this;
  }

  @Override
  public Config toConfig()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public DatabaseSpecificOverrideOptions toOptions()
  {
    return new DatabaseSpecificOverrideOptions(this);
  }

  public InformationSchemaViewsBuilder withInformationSchemaViews()
  {
    return informationSchemaViewsBuilder;
  }

  public DatabaseSpecificOverrideOptionsBuilder withoutIdentifierQuoteString()
  {
    identifierQuoteString = null;
    return this;
  }

  public DatabaseSpecificOverrideOptionsBuilder withoutSupportsCatalogs()
  {
    supportsCatalogs = Optional.empty();
    return this;
  }

  public DatabaseSpecificOverrideOptionsBuilder withoutSupportsSchemas()
  {
    supportsSchemas = Optional.empty();
    return this;
  }

  public DatabaseSpecificOverrideOptionsBuilder withTableColumnRetrievalStrategy(final MetadataRetrievalStrategy tableColumnRetrievalStrategy)
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

}

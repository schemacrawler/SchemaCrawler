package schemacrawler.schemacrawler;


import java.util.Map;

public class DatabaseSpecificOverrideOptionsBuilder
  implements OptionsBuilder<DatabaseSpecificOverrideOptions>
{

  private Boolean supportsSchemas;
  private Boolean supportsCatalogs;
  private boolean supportsFastColumnRetrieval;
  private boolean supportsFastForeignKeyRetrieval;
  private String identifierQuoteString;
  private final InformationSchemaViewsBuilder informationSchemaViewsBuilder;

  public DatabaseSpecificOverrideOptionsBuilder()
  {
    informationSchemaViewsBuilder = new InformationSchemaViewsBuilder();
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
    supportsCatalogs = Boolean.FALSE;
    return this;
  }

  /**
   * Overrides the JDBC driver provided information about whether the
   * database supports schema.
   */
  public DatabaseSpecificOverrideOptionsBuilder doesNotSupportSchemas()
  {
    supportsSchemas = Boolean.FALSE;
    return this;
  }

  @Override
  public DatabaseSpecificOverrideOptionsBuilder fromConfig(final Map<String, String> map)
  {
    informationSchemaViewsBuilder.fromConfig(map);
    return this;
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

  public Boolean getSupportsSchemas()
  {
    return supportsSchemas;
  }

  public Boolean getSupportsCatalogs()
  {
    return supportsCatalogs;
  }

  public boolean isSupportsFastColumnRetrieval()
  {
    return supportsFastColumnRetrieval;
  }

  public boolean isSupportsFastForeignKeyRetrieval()
  {
    return supportsFastForeignKeyRetrieval;
  }

  public String getIdentifierQuoteString()
  {
    return identifierQuoteString;
  }

  public InformationSchemaViewsBuilder getInformationSchemaViewsBuilder()
  {
    return informationSchemaViewsBuilder;
  }

  /**
   * Overrides the JDBC driver provided information about whether the
   * database supports catalogs.
   */
  public DatabaseSpecificOverrideOptionsBuilder supportsCatalogs()
  {
    supportsCatalogs = Boolean.TRUE;
    return this;
  }

  /**
   * Overrides the JDBC driver provided information about whether the
   * database supports schema.
   */
  public DatabaseSpecificOverrideOptionsBuilder supportsSchemas()
  {
    supportsSchemas = Boolean.TRUE;
    return this;
  }

  public DatabaseSpecificOverrideOptionsBuilder supportsFastForeignKeyRetrieval()
  {
    supportsFastForeignKeyRetrieval = true;
    return this;
  }

  public DatabaseSpecificOverrideOptionsBuilder supportsFastColumnRetrieval()
  {
    supportsFastColumnRetrieval = true;
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
    supportsCatalogs = null;
    return this;
  }

  public DatabaseSpecificOverrideOptionsBuilder withoutSupportsSchemas()
  {
    supportsSchemas = null;
    return this;
  }

}

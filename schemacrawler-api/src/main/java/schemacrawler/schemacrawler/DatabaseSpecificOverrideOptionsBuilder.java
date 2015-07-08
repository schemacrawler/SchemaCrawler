package schemacrawler.schemacrawler;


import java.util.Map;

public class DatabaseSpecificOverrideOptionsBuilder
  implements OptionsBuilder<DatabaseSpecificOverrideOptions>
{

  private static final String SC_OVERRIDE_SUPPORTS_SCHEMAS = "schemacrawler.override.supports_schemas";
  private static final String SC_OVERRIDE_SUPPORTS_CATALOGS = "schemacrawler.override.supports_catalogs";
  private static final String SC_OVERRIDE_SUPPORTS_IDENTIFIER_QUOTE_STRING = "schemacrawler.override.identifier_quote_string";

  private Boolean supportsSchemas;
  private Boolean supportsCatalogs;
  private String identifierQuoteString;
  private final InformationSchemaViewsBuilder informationSchemaViewsBuilder;

  public DatabaseSpecificOverrideOptionsBuilder()
  {
    informationSchemaViewsBuilder = new InformationSchemaViewsBuilder();
  }

  @Override
  public DatabaseSpecificOverrideOptionsBuilder fromConfig(final Map<String, String> map)
  {
    final Config config;
    if (map == null)
    {
      config = new Config();
    }
    else
    {
      config = new Config(map);
    }

    if (config.hasValue(SC_OVERRIDE_SUPPORTS_SCHEMAS))
    {
      final boolean supportsSchemasValue = config
        .getBooleanValue(SC_OVERRIDE_SUPPORTS_SCHEMAS);
      if (supportsSchemasValue)
      {
        supportsSchemas();
      }
    }
    if (config.hasValue(SC_OVERRIDE_SUPPORTS_CATALOGS))
    {
      final boolean supportsCatalogsValue = config
        .getBooleanValue(SC_OVERRIDE_SUPPORTS_CATALOGS);
      if (supportsCatalogsValue)
      {
        supportsCatalogs();
      }
    }
    if (config.hasValue(SC_OVERRIDE_SUPPORTS_IDENTIFIER_QUOTE_STRING))
    {
      identifierQuoteString(config
        .getStringValue(SC_OVERRIDE_SUPPORTS_IDENTIFIER_QUOTE_STRING, null));
    }

    informationSchemaViewsBuilder.fromConfig(map);

    return this;
  }

  /**
   * Overrides the JDBC driver provided information about the identifier quote
   * string.
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
   * Overrides the JDBC driver provided information about whether the database
   * supports catalogs.
   */
  public DatabaseSpecificOverrideOptionsBuilder supportsCatalogs()
  {
    supportsCatalogs = Boolean.TRUE;
    return this;
  }

  /**
   * Overrides the JDBC driver provided information about whether the database
   * supports schema.
   */
  public DatabaseSpecificOverrideOptionsBuilder supportsSchemas()
  {
    supportsSchemas = Boolean.TRUE;
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
    return new DatabaseSpecificOverrideOptions(supportsSchemas,
                                               supportsCatalogs,
                                               identifierQuoteString,
                                               informationSchemaViewsBuilder
                                                 .toOptions());
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

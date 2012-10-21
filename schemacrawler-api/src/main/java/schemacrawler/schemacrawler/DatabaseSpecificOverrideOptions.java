package schemacrawler.schemacrawler;


import sf.util.Utility;

public class DatabaseSpecificOverrideOptions
  implements Options
{

  private static final long serialVersionUID = -5593417085363698921L;

  private static final String SC_OVERRIDE_SUPPORTS_SCHEMAS = "schemacrawler.override.supports_schemas";
  private static final String SC_OVERRIDE_SUPPORTS_CATALOGS = "schemacrawler.override.supports_catalogs";
  private static final String SC_OVERRIDE_SUPPORTS_IDENTIFIER_QUOTE_STRING = "schemacrawler.override.identifier_quote_string";

  private Boolean supportsSchemas;
  private Boolean supportsCatalogs;
  private String identifierQuoteString;

  public DatabaseSpecificOverrideOptions()
  {
    // Default: No overrides - all are null
  }

  public DatabaseSpecificOverrideOptions(final Config config)
  {
    this();
    final Config configProperties;
    if (config == null)
    {
      configProperties = new Config();
    }
    else
    {
      configProperties = config;
    }

    if (configProperties.hasValue(SC_OVERRIDE_SUPPORTS_SCHEMAS))
    {
      setSupportsSchemas(configProperties
        .getBooleanValue(SC_OVERRIDE_SUPPORTS_SCHEMAS));
    }
    if (configProperties.hasValue(SC_OVERRIDE_SUPPORTS_CATALOGS))
    {
      setSupportsCatalogs(configProperties
        .getBooleanValue(SC_OVERRIDE_SUPPORTS_CATALOGS));
    }
    if (configProperties.hasValue(SC_OVERRIDE_SUPPORTS_IDENTIFIER_QUOTE_STRING))
    {
      setIdentifierQuoteString(configProperties
        .getStringValue(SC_OVERRIDE_SUPPORTS_IDENTIFIER_QUOTE_STRING, null));
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

  public boolean hasOverrideForIdentifierQuoteString()
  {
    return !Utility.isBlank(identifierQuoteString);
  }

  public boolean hasOverrideForSupportsCatalogs()
  {
    return supportsCatalogs != null;
  }

  public boolean hasOverrideForSupportsSchemas()
  {
    return supportsSchemas != null;
  }

  public boolean isSupportsCatalogs()
  {
    if (supportsCatalogs == null)
    {
      return true;
    }
    return supportsCatalogs;
  }

  public boolean isSupportsSchemas()
  {
    if (supportsSchemas == null)
    {
      return true;
    }
    return supportsSchemas;
  }

  /**
   * Overrides the JDBC driver provided information about the identifier
   * quote string.
   * 
   * @param getIdentifierQuoteString
   *        Value for the override
   */
  public void setIdentifierQuoteString(final String identifierQuoteString)
  {
    this.identifierQuoteString = identifierQuoteString;
  }

  /**
   * Overrides the JDBC driver provided information about whether the
   * database supports catalogs.
   * 
   * @param isSupportsCatalogs
   *        Value for the override
   */
  public void setSupportsCatalogs(final boolean supportsCatalogs)
  {
    this.supportsCatalogs = supportsCatalogs;
  }

  /**
   * Overrides the JDBC driver provided information about whether the
   * database supports schema.
   * 
   * @param isSupportsSchemas
   *        Value for the override
   */
  public void setSupportsSchemas(final boolean supportsSchemas)
  {
    this.supportsSchemas = supportsSchemas;
  }

  public void unsetIdentifierQuoteString()
  {
    identifierQuoteString = null;
  }

  public void unsetSupportsCatalogs()
  {
    supportsCatalogs = null;
  }

  public void unsetSupportsSchemas()
  {
    supportsSchemas = null;
  }

}

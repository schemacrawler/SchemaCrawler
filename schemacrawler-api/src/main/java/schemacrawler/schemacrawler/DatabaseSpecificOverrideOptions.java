package schemacrawler.schemacrawler;


import static sf.util.Utility.isBlank;

public final class DatabaseSpecificOverrideOptions
  implements Options
{

  private static final long serialVersionUID = -5593417085363698921L;

  private final Boolean supportsSchemas;
  private final Boolean supportsCatalogs;
  private final Boolean supportsReservedWords;
  private final String identifierQuoteString;
  private final InformationSchemaViews informationSchemaViews;

  public DatabaseSpecificOverrideOptions()
  {
    this(null, null, null, null, null);
  }

  protected DatabaseSpecificOverrideOptions(final Boolean supportsSchemas,
                                            final Boolean supportsCatalogs,
                                            final Boolean supportsReservedWords,
                                            final String identifierQuoteString,
                                            final InformationSchemaViews informationSchemaViews)
  {
    this.supportsSchemas = supportsSchemas;
    this.supportsCatalogs = supportsCatalogs;
    this.supportsReservedWords = supportsReservedWords;
    this.identifierQuoteString = identifierQuoteString;
    this.informationSchemaViews = informationSchemaViews == null? new InformationSchemaViews()
                                                                : informationSchemaViews;
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

  public boolean hasOverrideForIdentifierQuoteString()
  {
    return !isBlank(identifierQuoteString);
  }

  public boolean hasOverrideForSupportsCatalogs()
  {
    return supportsCatalogs != null;
  }

  public boolean hasOverrideForSupportsSchemas()
  {
    return supportsSchemas != null;
  }

  public boolean hasOverrideForSupportsReservedWords()
  {
    return supportsReservedWords != null;
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

  public boolean isSupportsReservedWords()
  {
    if (supportsReservedWords == null)
    {
      return true;
    }
    return supportsReservedWords;
  }

}

package schemacrawler.schemacrawler;


import static sf.util.Utility.isBlank;

public final class DatabaseSpecificOverrideOptions
  implements Options
{

  private static final long serialVersionUID = -5593417085363698921L;

  private final Boolean supportsSchemas;
  private final Boolean supportsCatalogs;
  private final boolean supportsFastColumnRetrieval;
  private final boolean supportsFastForeignKeyRetrieval;
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
      supportsSchemas = null;
      supportsCatalogs = null;
      supportsFastColumnRetrieval = false;
      supportsFastForeignKeyRetrieval = false;
      identifierQuoteString = "";
      informationSchemaViews = new InformationSchemaViews();
    }
    else
    {
      supportsSchemas = builder.getSupportsSchemas();
      supportsCatalogs = builder.getSupportsCatalogs();
      supportsFastColumnRetrieval = builder.isSupportsFastColumnRetrieval();
      supportsFastForeignKeyRetrieval = builder
        .isSupportsFastForeignKeyRetrieval();
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

  public boolean isSupportsCatalogs()
  {
    if (supportsCatalogs == null)
    {
      return true;
    }
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

  public boolean isSupportsSchemas()
  {
    if (supportsSchemas == null)
    {
      return true;
    }
    return supportsSchemas;
  }

}

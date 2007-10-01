package schemacrawler.tools.integration.spring;


import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.sql.DataSource;

import schemacrawler.crawl.InformationSchemaViews;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.crawl.SchemaInfoLevel;
import schemacrawler.schema.Schema;
import schemacrawler.tools.OutputFormat;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.schematext.SchemaTextOptions;

public class SchemaCrawlerBean
  implements Serializable
{

  private static final long serialVersionUID = 2945309814993843757L;

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawlerBean.class
    .getName());

  private DataSource dataSource;
  private SchemaCrawlerOptions schemaCrawlerOptions;
  private SchemaTextOptions schemaTextOptions;
  private OutputOptions outputOptions;
  private InformationSchemaViews informationSchemaViews;

  public SchemaCrawlerBean()
  {
    schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaTextOptions = new SchemaTextOptions();
    outputOptions = new OutputOptions();
    informationSchemaViews = new InformationSchemaViews(new HashMap<String, String>());
  }

  public OutputOptions getOutputOptions()
  {
    return outputOptions;
  }

  public void setOutputOptions(OutputOptions outputOptions)
  {
    this.outputOptions = outputOptions;
  }

  public DataSource getDataSource()
  {
    return dataSource;
  }

  public void setDataSource(DataSource dataSource)
  {
    this.dataSource = dataSource;
  }

  public SchemaCrawlerOptions getSchemaCrawlerOptions()
  {
    return schemaCrawlerOptions;
  }

  public void setSchemaCrawlerOptions(SchemaCrawlerOptions schemaCrawlerOptions)
  {
    this.schemaCrawlerOptions = schemaCrawlerOptions;
  }

  public SchemaTextOptions getSchemaTextOptions()
  {
    return schemaTextOptions;
  }

  public void setSchemaTextOptions(SchemaTextOptions schemaTextOptions)
  {
    this.schemaTextOptions = schemaTextOptions;
  }

  public InformationSchemaViews getInformationSchemaViews()
  {
    return informationSchemaViews;
  }

  public void setInformationSchemaViews(InformationSchemaViews informationSchemaViews)
  {
    this.informationSchemaViews = informationSchemaViews;
  }

  private void checkDataSouce()
  {
    if (dataSource == null)
    {
      throw new IllegalStateException("A data-source needs to be provided");
    }
  }

  /**
   * Gets the entire schema.
   * 
   * @param infoLevel
   *        Schema info level
   * @return Schema
   */
  public Schema getSchema(final SchemaInfoLevel infoLevel)
  {
    checkDataSouce();
    return SchemaCrawler.getSchema(dataSource, infoLevel, schemaCrawlerOptions);
  }

  /**
   * Gets the entire schema.
   * 
   * @param infoLevel
   *        Schema info level
   * @return Schema
   */
  public Schema printSchema(final SchemaInfoLevel infoLevel,
                            final OutputFormat outputFormat)
  {
    checkDataSouce();
    return SchemaCrawler.getSchema(dataSource, infoLevel, schemaCrawlerOptions);
  }

}

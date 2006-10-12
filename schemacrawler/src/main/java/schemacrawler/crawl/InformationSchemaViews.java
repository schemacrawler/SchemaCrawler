package schemacrawler.crawl;


import java.util.Properties;

import schemacrawler.BaseOptions;

final class InformationSchemaViews
  extends BaseOptions
{

  private static final long serialVersionUID = 3587581365346059044L;

  private Properties informationSchemaViewsSql;

  InformationSchemaViews(Properties informationSchemaViewsSql)
  {
    if (informationSchemaViewsSql != null)
    {
      this.informationSchemaViewsSql = informationSchemaViewsSql;
    }
    else
    {
      this.informationSchemaViewsSql = new Properties();
    }
  }

  /**
   * Gets the view definitions SQL from the additional configuration.
   * 
   * @return View defnitions SQL.
   */
  String getViewsSql()
  {
    return informationSchemaViewsSql
      .getProperty("select.INFORMATION_SCHEMA.VIEWS");
  }

  /**
   * Gets the procedure definitions SQL from the additional
   * configuration.
   * 
   * @return Procedure defnitions SQL.
   */
  String getRoutinesSql()
  {
    return informationSchemaViewsSql
      .getProperty("select.INFORMATION_SCHEMA.ROUTINES");
  }

  /**
   * Gets the table constraints SQL from the additional configuration.
   * 
   * @return Table constraints SQL.
   */
  String getTableConstraintsSql()
  {
    return informationSchemaViewsSql
      .getProperty("select.INFORMATION_SCHEMA.TABLE_CONSTRAINTS");
  }

  /**
   * Gets the table check constraints SQL from the additional
   * configuration.
   * 
   * @return Table check constraints SQL.
   */
  String getCheckConstraintsSql()
  {
    return informationSchemaViewsSql
      .getProperty("select.INFORMATION_SCHEMA.CHECK_CONSTRAINTS");
  }

}

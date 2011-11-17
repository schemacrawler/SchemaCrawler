package schemacrawler.tools.text.base;


import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;

public interface SchemaFormatter
  extends Formatter
{

  void handle(final ColumnDataType columnDataType)
    throws SchemaCrawlerException;

  /**
   * Provides information on the database schema.
   * 
   * @param procedure
   *        Procedure metadata.
   */
  void handle(final Procedure procedure)
    throws SchemaCrawlerException;

  /**
   * Provides information on the database schema.
   * 
   * @param table
   *        Table metadata.
   */
  void handle(final Table table)
    throws SchemaCrawlerException;

  void handleColumnDataTypesEnd()
    throws SchemaCrawlerException;

  void handleColumnDataTypesStart()
    throws SchemaCrawlerException;

  void handleProceduresEnd()
    throws SchemaCrawlerException;

  void handleProceduresStart()
    throws SchemaCrawlerException;

  void handleTablesEnd()
    throws SchemaCrawlerException;

  void handleTablesStart()
    throws SchemaCrawlerException;

}

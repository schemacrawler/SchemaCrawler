package schemacrawler;


import javax.sql.DataSource;

/**
 * Executor for main functionality.
 * 
 * @author Sualeh Fatehi
 */
public interface Executor
{

  /**
   * Executes main functionality for SchemaCrawler.
   * 
   * @param options
   *          Options
   * @param dataSource
   *          Datasource
   * @throws Exception
   *           On an exception
   */
  void execute(final Options options, final DataSource dataSource)
    throws Exception;

}

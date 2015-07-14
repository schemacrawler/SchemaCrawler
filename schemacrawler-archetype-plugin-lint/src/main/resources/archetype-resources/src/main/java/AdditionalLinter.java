#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};


import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;

public class AdditionalLinter
  extends BaseLinter
{

  private static final Logger LOGGER = Logger.getLogger(AdditionalLinter.class.getName());
	  
  @Override
  public String getDescription()
  {
    return getSummary();
  }

  @Override
  public String getSummary()
  {
    return "table names should start with FOO_";
  }

  @Override
  protected void lint(final Table table)
  {
    if (table != null)
    {
      if (!table.getName().startsWith("FOO_"))
      {
      	// SchemaCrawler will control output of log messages if you use JDK logging
      	LOGGER.log(Level.INFO, "Adding lint for table, " + table);    	  
        addLint(table, getSummary(), table.getFullName());
      }
    }
  }

}

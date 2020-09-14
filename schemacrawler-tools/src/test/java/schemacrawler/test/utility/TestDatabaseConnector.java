package schemacrawler.test.utility;


import java.io.IOException;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.tools.databaseconnector.DatabaseConnectionUrlBuilder;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.ioresource.ClasspathInputResource;

/**
 * SchemaCrawler database support plug-in.
 * <p>
 * Plug-in to support a hypothetical RMDBS, "Test Database".
 *
 * @see <a href="https://www.schemacrawler.com">SchemaCrawler</a>
 */
public final class TestDatabaseConnector
  extends DatabaseConnector
{

  private static final DatabaseServerType DB_SERVER_TYPE =
    new DatabaseServerType("test-db", "Test Database");

  public TestDatabaseConnector()
    throws IOException
  {
    super(DB_SERVER_TYPE,
          new ClasspathInputResource(
            "/META-INF/schemacrawler-test-db.config.properties"),
          (informationSchemaViewsBuilder, connection) -> informationSchemaViewsBuilder.fromResourceFolder(
            "/test-db.information_schema"),
          (schemaRetrievalOptionsBuilder, connection) -> {},
          (limitOptionsBuilder, connection) -> {},
          () -> DatabaseConnectionUrlBuilder.builder(""));
  }

  @Override
  public PluginCommand getHelpCommand()
  {
    final PluginCommand pluginCommand = super.getHelpCommand();
    pluginCommand.addOption("server",
                            "--server=test-db%n"
                            + "Loads SchemaCrawler plug-in for Test Database",
                            String.class);
    return pluginCommand;
  }

  @Override
  protected Predicate<String> supportsUrlPredicate()
  {
    return url -> Pattern.matches("jdbc:test-db:.*", url);
  }
  
}

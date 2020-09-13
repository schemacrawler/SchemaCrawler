package schemacrawler.test.utility;


import static us.fatehi.utility.Utility.isBlank;
import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.DatabaseServerType;
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
          (limitOptionsBuilder, connection) -> {});
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

  @Override
  protected String constructConnectionUrl(final String providedHost,
      final Integer providedPort, final String providedDatabase,
      final Map<String, String> urlx)
  {

    final String defaultHost = "localhost";
    final int defaultPort = 1234;
    final String defaultDatabase = "";
    final String urlFormat = "jdbc:test-db:host=%s;port=%d;database=%d";

    final String host;
    if (isBlank(providedHost))
    {
      host = defaultHost;
    } else
    {
      host = providedHost;
    }

    final int port;
    if (providedPort == null || providedPort < 0 || providedPort > 65535)
    {
      port = defaultPort;
    } else
    {
      port = providedPort;
    }

    final String database;
    if (isBlank(providedDatabase))
    {
      database = defaultDatabase;
    } else
    {
      database = providedDatabase;
    }

    final String url = String.format(urlFormat, host, port, database);

    return url;
  }
  
}

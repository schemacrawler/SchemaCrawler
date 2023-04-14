package us.fatehi.utility.datasource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import us.fatehi.test.utility.TestConnection;

public class DatabaseConnectionSourceBuilderTest {

  @Test
  public void connectionInitializer() throws SQLException {

    final DatabaseConnectionSourceBuilder builder =
        DatabaseConnectionSourceBuilder.builder("jdbc:test-db:${host}:${port}/${database}");

    assertThat(builder.getConnectionInitializer(), is(not(nullValue())));

    builder.withConnectionInitializer(null);
    assertThat(builder.getConnectionInitializer(), is(not(nullValue())));

    final Consumer<Connection> connectionInitializer = connection -> {};

    builder.withConnectionInitializer(connectionInitializer);
    assertThat(builder.getConnectionInitializer(), is(connectionInitializer));
  }

  @Test
  public void credentials() throws SQLException {

    final DatabaseConnectionSourceBuilder builder =
        DatabaseConnectionSourceBuilder.builder("jdbc:test-db:${host}:${port}/${database}");

    TestConnection connection;

    builder.withUserCredentials(null);
    connection = builder.build().get().unwrap(TestConnection.class);
    assertThat(connection.getUrl(), is("jdbc:test-db:localhost:0/"));
    assertThat(connection.getConnectionProperties(), is(anEmptyMap()));

    builder.withUserCredentials(new MultiUseUserCredentials("dbuser", "strongpassword"));
    connection = builder.build().get().unwrap(TestConnection.class);
    assertThat(connection.getConnectionProperties(), hasEntry("user", "dbuser"));
    assertThat(connection.getConnectionProperties(), hasEntry("password", "strongpassword"));
  }

  @Test
  public void database() {
    final DatabaseConnectionSourceBuilder builder =
        DatabaseConnectionSourceBuilder.builder("jdbc:test-db:${host}:${port}/${database}");

    assertThat(builder.toURL(), is("jdbc:test-db:localhost:0/"));

    builder.withDefaultDatabase("default-database");
    assertThat(builder.toURL(), is("jdbc:test-db:localhost:0/default-database"));

    builder.withDatabase("database");
    assertThat(builder.toURL(), is("jdbc:test-db:localhost:0/database"));

    builder.withDatabase("  ");
    assertThat(builder.toURL(), is("jdbc:test-db:localhost:0/default-database"));
  }

  @Test
  public void host() {
    final DatabaseConnectionSourceBuilder builder =
        DatabaseConnectionSourceBuilder.builder("jdbc:test-db:${host}:${port}/${database}");

    assertThat(builder.toURL(), is("jdbc:test-db:localhost:0/"));

    builder.withDefaultHost("default-host");
    assertThat(builder.toURL(), is("jdbc:test-db:default-host:0/"));

    builder.withHost("host");
    assertThat(builder.toURL(), is("jdbc:test-db:host:0/"));

    builder.withHost("  ");
    assertThat(builder.toURL(), is("jdbc:test-db:default-host:0/"));
  }

  @Test
  public void port() {
    final DatabaseConnectionSourceBuilder builder =
        DatabaseConnectionSourceBuilder.builder("jdbc:test-db:${host}:${port}/${database}");

    assertThat(builder.toURL(), is("jdbc:test-db:localhost:0/"));

    builder.withDefaultPort(2121);
    assertThat(builder.toURL(), is("jdbc:test-db:localhost:2121/"));

    builder.withPort(2222);
    assertThat(builder.toURL(), is("jdbc:test-db:localhost:2222/"));

    builder.withPort(-1);
    assertThat(builder.toURL(), is("jdbc:test-db:localhost:2121/"));

    builder.withPort(65599);
    assertThat(builder.toURL(), is("jdbc:test-db:localhost:2121/"));
  }

  @Test
  public void urlx() throws SQLException {

    final Map<String, String> defaultMap = new HashMap<>();
    defaultMap.put("key", "default-value");

    final Map<String, String> map = new HashMap<>();
    map.put("key", "value");

    final DatabaseConnectionSourceBuilder builder =
        DatabaseConnectionSourceBuilder.builder("jdbc:test-db:${host}:${port}/${database}");

    assertThat(builder.toUrlx(), is(new HashMap<>()));

    builder.withDefaultUrlx(defaultMap);
    assertThat(builder.toUrlx(), is(defaultMap));

    builder.withUrlx(map);
    assertThat(builder.toUrlx(), is(map));
    final TestConnection connection = builder.build().get().unwrap(TestConnection.class);
    assertThat(connection.getConnectionProperties(), hasEntry("key", "value"));

    builder.withUrlx(null);
    assertThat(builder.toUrlx(), is(defaultMap));

    builder.withDefaultUrlx(null);
    assertThat(builder.toUrlx(), is(anEmptyMap()));

    builder.withDefaultUrlx("newkey", true);
    assertThat(builder.toUrlx(), hasEntry("newkey", "true"));
  }
}

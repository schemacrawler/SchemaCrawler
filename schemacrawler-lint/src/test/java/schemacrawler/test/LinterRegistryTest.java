package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.lint.LinterRegistry;
import us.fatehi.utility.property.PropertyName;

public class LinterRegistryTest {

  @Test
  public void registeredPlugins() throws Exception {
    final Collection<PropertyName> supportedLinters =
        LinterRegistry.getLinterRegistry().getRegisteredPlugins();
    assertThat(supportedLinters, hasSize(22));
  }

  @Test
  public void name() throws Exception {
    final LinterRegistry catalogLoaderRegistry = LinterRegistry.getLinterRegistry();
    assertThat(catalogLoaderRegistry.getName(), is("Linters"));
  }
}

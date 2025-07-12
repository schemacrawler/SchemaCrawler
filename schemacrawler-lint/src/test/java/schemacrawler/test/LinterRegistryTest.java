/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

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
    assertThat(supportedLinters, hasSize(23));
  }

  @Test
  public void name() throws Exception {
    final LinterRegistry catalogLoaderRegistry = LinterRegistry.getLinterRegistry();
    assertThat(catalogLoaderRegistry.getName(), is("Linters"));
  }
}

/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.scribe.command.options.ScribeOptionsBuilder;
import schemacrawler.scribe.renderer.ScribeSupport;
import schemacrawler.test.utility.StubExecutionState;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.lint.Lints;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class CrossReferenceModelFactoryTest {

  @Test
  public void createsModelWithoutSelfReferencesOrDuplicates(
      final DatabaseConnectionSource connectionSource) {
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    final ScribeSupport support =
        new ScribeSupport(
            new StubExecutionState(catalog),
            ScribeOptionsBuilder.builder().toOptions(),
            new Lints(List.of()));

    final List<CrossReferenceEntry> entries =
        CrossReferenceModelFactory.createCrossReferenceModel(support);
    assertThat(entries.size(), is(greaterThan(0)));

    final Set<String> uniquePairs = new HashSet<>();
    for (final CrossReferenceEntry entry : entries) {
      assertThat(entry.databaseObject(), is(notNullValue()));
      assertThat(entry.usedByDatabaseObject(), is(notNullValue()));
      assertThat(entry.databaseObjectType(), is(notNullValue()));
      assertThat(entry.usedByDatabaseObjectType(), is(notNullValue()));
      assertThat(entry.databaseObject().equals(entry.usedByDatabaseObject()), is(false));

      final String pairKey =
          entry.databaseObject().getFullName() + "->" + entry.usedByDatabaseObject().getFullName();
      assertThat(uniquePairs.add(pairKey), is(true));
    }
  }
}

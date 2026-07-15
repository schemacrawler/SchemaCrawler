/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.output;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import org.junit.jupiter.api.Test;
import schemacrawler.scribe.renderer.ScribeMessages;

public class ScribeMessagesTest {

  @Test
  public void englishIsEnglish() {
    final ScribeMessages messages = new ScribeMessages(Locale.ENGLISH);
    assertThat(messages.sectionColumns(), is("Columns"));
    assertThat(messages.headerName(), is("Name"));
    assertThat(messages.headerAttribute(), is("Attribute"));
    assertThat(messages.triggerAttributeTiming(), is("Timing"));
    assertThat(messages.navTables(), is("Tables"));
    assertThat(messages.labelFunction(), is("Function"));
    assertThat(messages.labelStoredProcedure(), is("Stored Procedure"));
    assertThat(messages.labelDatabaseProduct(), is("Database product"));
    assertThat(messages.labelDatabaseVersion(), is("Database version"));
    assertThat(messages.labelDatabaseSchema(), is("Database Schema"));
    assertThat(messages.labelTables(), is("Tables"));
    assertThat(messages.labelEntityModelType(), is("Entity model type"));
    assertThat(messages.valueEntityModelTypeBridgeTable(), is("Bridge table"));
  }

  @Test
  public void frenchIsLocalized() {
    final ScribeMessages messages = new ScribeMessages(Locale.FRENCH);
    assertThat(messages.sectionColumns(), is(not("Columns")));
    assertThat(messages.labelDatabaseSchema(), is(not("Database Schema")));
  }

  @Test
  public void germanIsLocalized() {
    final ScribeMessages messages = new ScribeMessages(Locale.GERMAN);
    assertThat(messages.sectionColumns(), is(not("Columns")));
    assertThat(messages.labelDatabaseSchema(), is(not("Database Schema")));
  }

  @Test
  public void spanishIsLocalized() {
    final ScribeMessages messages = new ScribeMessages(Locale.forLanguageTag("es"));
    assertThat(messages.sectionColumns(), is(not("Columns")));
    assertThat(messages.labelDatabaseSchema(), is(not("Database Schema")));
  }

  @Test
  public void unknownLocaleFallsBackToEnglish() {
    final ScribeMessages messages = new ScribeMessages(Locale.forLanguageTag("xx"));
    assertThat(messages.sectionColumns(), is("Columns"));
  }

  @Test
  public void everyBundleHasNoLogKeysAndSameKeySetAsEnglish() throws IOException {
    final Set<String> englishKeys = loadKeys("ScribeMessages.properties");
    for (final String key : englishKeys) {
      assertThat(key.startsWith("log."), is(false));
    }

    for (final String bundleFile :
        new String[] {
          "ScribeMessages_fr.properties",
          "ScribeMessages_de.properties",
          "ScribeMessages_es.properties"
        }) {
      final Set<String> localizedKeys = loadKeys(bundleFile);
      assertThat(bundleFile, localizedKeys, is(englishKeys));
    }
  }

  private Set<String> loadKeys(final String resourceName) throws IOException {
    final Properties properties = new Properties();
    try (InputStream inputStream =
        getClass().getResourceAsStream("/schemacrawler/scribe/i18n/" + resourceName)) {
      properties.load(inputStream);
    }
    return properties.stringPropertyNames();
  }
}

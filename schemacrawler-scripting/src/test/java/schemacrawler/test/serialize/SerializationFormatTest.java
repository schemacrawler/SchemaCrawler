/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.serialize;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;

import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.serialize.options.SerializationFormat;
import schemacrawler.tools.formatter.serialize.CatalogSerializer;
import schemacrawler.tools.options.ConfigUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class SerializationFormatTest {

  @Test
  public void fromFormatBlank() {
    assertThat(SerializationFormat.fromFormat(""), is(SerializationFormat.ser));
  }

  @Test
  public void fromFormatCompactJson() {
    assertThat(
        SerializationFormat.fromFormat("compact_json"), is(SerializationFormat.compact_json));
  }

  @Test
  public void fromFormatJson() {
    assertThat(SerializationFormat.fromFormat("json"), is(SerializationFormat.json));
  }

  @Test
  public void fromFormatNull() {
    assertThat(SerializationFormat.fromFormat(null), is(SerializationFormat.ser));
  }

  @Test
  public void fromFormatSer() {
    assertThat(SerializationFormat.fromFormat("ser"), is(SerializationFormat.ser));
  }

  @Test
  public void fromFormatUnknown() {
    assertThat(SerializationFormat.fromFormat("unknown_format"), is(SerializationFormat.ser));
  }

  @Test
  public void fromFormatYaml() {
    assertThat(SerializationFormat.fromFormat("yaml"), is(SerializationFormat.yaml));
  }

  @Test
  public void getDescription() {
    for (final SerializationFormat format : SerializationFormat.values()) {
      assertThat(format.getDescription(), is(notNullValue()));
    }
  }

  @Test
  public void getFileExtension() {
    for (final SerializationFormat format : SerializationFormat.values()) {
      assertThat(format.getFileExtension(), is(notNullValue()));
    }
  }

  @Test
  public void getFormat() {
    for (final SerializationFormat format : SerializationFormat.values()) {
      assertThat(format.getFormat(), is(notNullValue()));
    }
  }

  @Test
  public void getFormats() {
    for (final SerializationFormat format : SerializationFormat.values()) {
      assertThat(format.getFormats(), is(not(empty())));
    }
  }

  @Test
  public void isBinaryFormatCompactJson() {
    assertThat(SerializationFormat.compact_json.isBinaryFormat(), is(false));
  }

  @Test
  public void isBinaryFormatJson() {
    assertThat(SerializationFormat.json.isBinaryFormat(), is(false));
  }

  @Test
  public void isBinaryFormatSer() {
    assertThat(SerializationFormat.ser.isBinaryFormat(), is(true));
  }

  @Test
  public void isBinaryFormatYaml() {
    assertThat(SerializationFormat.yaml.isBinaryFormat(), is(false));
  }

  @Test
  public void isSupportedFormatBlank() {
    assertThat(SerializationFormat.isSupportedFormat(""), is(false));
  }

  @Test
  public void isSupportedFormatJson() {
    assertThat(SerializationFormat.isSupportedFormat("json"), is(true));
  }

  @Test
  public void isSupportedFormatNull() {
    assertThat(SerializationFormat.isSupportedFormat(null), is(false));
  }

  @Test
  public void isSupportedFormatSer() {
    assertThat(SerializationFormat.isSupportedFormat("ser"), is(true));
  }

  @Test
  public void isSupportedFormatUnknown() {
    assertThat(SerializationFormat.isSupportedFormat("unknown"), is(false));
  }

  @Test
  public void newSerializerAllFormats(final DatabaseConnectionSource connectionSource)
      throws Exception {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final Catalog catalog =
        getCatalog(
            connectionSource,
            schemaRetrievalOptionsDefault,
            schemaCrawlerOptions,
            ConfigUtility.newConfig());

    for (final SerializationFormat format : SerializationFormat.values()) {
      final CatalogSerializer serializer = format.newSerializer(catalog);
      assertThat(serializer, is(notNullValue()));
      assertThat(serializer.getCatalog(), is(notNullValue()));
    }
  }

  @Test
  public void toStringNotNull() {
    for (final SerializationFormat format : SerializationFormat.values()) {
      assertThat(format.toString(), is(notNullValue()));
    }
  }
}

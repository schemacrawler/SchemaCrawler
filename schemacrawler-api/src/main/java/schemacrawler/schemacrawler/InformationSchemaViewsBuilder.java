/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.IOUtility.readResourceFully;
import static us.fatehi.utility.Utility.isBlank;

import java.sql.Connection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import us.fatehi.utility.TemplatingUtility;

/** The database specific views to get additional database metadata in a standard format. */
public final class InformationSchemaViewsBuilder
    implements OptionsBuilder<InformationSchemaViewsBuilder, InformationSchemaViews> {

  public static InformationSchemaViewsBuilder builder() {
    return new InformationSchemaViewsBuilder();
  }

  public static InformationSchemaViewsBuilder builder(
      final InformationSchemaViews informationSchemaViews) {
    return new InformationSchemaViewsBuilder().fromOptions(informationSchemaViews);
  }

  public static InformationSchemaViews newInformationSchemaViews() {
    return new InformationSchemaViews();
  }

  private final Map<InformationSchemaKey, String> informationSchemaQueries;

  private InformationSchemaViewsBuilder() {
    informationSchemaQueries = new EnumMap<>(InformationSchemaKey.class);
  }

  @Override
  public InformationSchemaViewsBuilder fromOptions(
      final InformationSchemaViews informationSchemaViews) {
    if (informationSchemaViews == null) {
      return this;
    }

    informationSchemaQueries.putAll(informationSchemaViews.getAllInformationSchemaViews());

    return this;
  }

  /**
   * Information schema views from a map.
   *
   * @param classpath Classpath location for SQL queries.
   * @return Builder
   */
  public InformationSchemaViewsBuilder fromResourceFolder(final String classpath) {
    if (isBlank(classpath)) {
      return this;
    }

    for (final InformationSchemaKey key : InformationSchemaKey.values()) {
      final String resource = String.format("%s/%s.sql", classpath, key);
      final String sql = readResourceFully(resource);
      if (!isBlank(sql)) {
        informationSchemaQueries.put(key, sql);
      }
    }

    return this;
  }

  public void substituteAll(final String templateKey, final String templateValue) {
    final Map<String, String> map = new HashMap<>();
    map.put(templateKey, templateValue);
    for (final Map.Entry<InformationSchemaKey, String> query :
        informationSchemaQueries.entrySet()) {
      String sql = query.getValue();
      sql = TemplatingUtility.expandTemplate(sql, map);
      query.setValue(sql);
    }
  }

  @Override
  public InformationSchemaViews toOptions() {
    return new InformationSchemaViews(informationSchemaQueries);
  }

  public InformationSchemaViewsBuilder withFunction(
      final BiConsumer<InformationSchemaViewsBuilder, Connection>
          informationSchemaViewsBuilderForConnection,
      final Connection connection) {
    if (informationSchemaViewsBuilderForConnection != null) {
      informationSchemaViewsBuilderForConnection.accept(this, connection);
    }
    return this;
  }

  /**
   * Sets definitions SQL.
   *
   * @param key SQL query key
   * @param sql Definitions SQL.
   * @return Builder
   */
  public InformationSchemaViewsBuilder withSql(final InformationSchemaKey key, final String sql) {
    requireNonNull(key, "No key provided");
    if (isBlank(sql)) {
      informationSchemaQueries.remove(key);
    } else {
      informationSchemaQueries.put(key, sql);
    }
    return this;
  }
}

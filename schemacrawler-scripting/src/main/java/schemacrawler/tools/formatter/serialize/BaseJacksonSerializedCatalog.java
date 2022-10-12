/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.tools.formatter.serialize;

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;
import static com.fasterxml.jackson.databind.SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_ENUMS_USING_TO_STRING;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;

/** Decorates a database to allow for serialization to and from plain Java serialization. */
public abstract class BaseJacksonSerializedCatalog implements CatalogSerializer {

  private static class IgnoreExceptionBeanPropertyFilter extends SimpleBeanPropertyFilter {

    private static final List<String> PARTIAL_PROPERTIES =
        Arrays.asList(
            "name", "short-name", "full-name", "attributes", "parent-partial", "remarks", "schema");

    @Override
    public void serializeAsField(
        final Object pojo,
        final JsonGenerator jgen,
        final SerializerProvider provider,
        final PropertyWriter writer)
        throws Exception {
      if (include(writer)) {
        try {
          if (pojo instanceof PartialDatabaseObject
              && !PARTIAL_PROPERTIES.contains(writer.getName())) {
            return;
          }
          writer.serializeAsField(pojo, jgen, provider);
        } catch (final Exception e) {
          LOGGER.log(Level.FINE, e.getMessage(), e);
          return;
        }
      } else if (!jgen.canOmitFields()) {
        writer.serializeAsOmittedField(pojo, jgen, provider);
      }
    }

    @Override
    protected boolean include(final BeanPropertyWriter writer) {
      return true;
    }

    @Override
    protected boolean include(final PropertyWriter writer) {
      return true;
    }
  }

  private static final Logger LOGGER =
      Logger.getLogger(BaseJacksonSerializedCatalog.class.getName());

  private final Catalog catalog;
  private final SortedSet<Column> allTableColumns;

  public BaseJacksonSerializedCatalog(final Catalog catalog) {
    this.catalog = requireNonNull(catalog, "No catalog provided");
    allTableColumns = new TreeSet<>();
    loadAllTableColumns();
  }

  public Set<Column> getAllTableColumns() {
    return new TreeSet<>(allTableColumns);
  }

  @Override
  public Catalog getCatalog() {
    return catalog;
  }

  /** {@inheritDoc} */
  @Override
  public void save(final OutputStream out) {
    requireNonNull(out, "No output stream provided");
    save(new OutputStreamWriter(out, UTF_8));
  }

  /** {@inheritDoc} */
  @Override
  public void save(final Writer out) {
    requireNonNull(out, "No writer provided");
    try {
      final ObjectMapper mapper = newConfiguredObjectMapper();
      mapper.writeValue(out, this);
    } catch (final IOException e) {
      throw new IORuntimeException("Could not serialize catalog", e);
    }
  }

  protected abstract ObjectMapper newObjectMapper();

  private void loadAllTableColumns() {
    for (final Table table : catalog.getTables()) {
      for (final Column column : table.getColumns()) {
        allTableColumns.add(column);
      }
    }
  }

  private ObjectMapper newConfiguredObjectMapper() {

    @JsonIgnoreProperties({
      "parent",
      "referenced-column",
      "exported-foreign-keys",
      "imported-foreign-keys"
    })
    @JsonPropertyOrder(
        value = {
          "@uuid",
          "name",
          "short-name",
          "full-name",
          "crawl-info",
          "schema-crawler-info",
          "jvm-system-info",
          "operating-system-info",
          "database-info",
          "jdbc-driver-info",
          "schemas",
          "system-column-data-types",
          "column-data-types",
          "all-table-columns"
        },
        alphabetic = true)
    @JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property = "@uuid")
    @JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
    @JsonFilter("ignore-getter-errors-filter")
    class JacksonAnnotationMixIn {}

    final FilterProvider filters =
        new SimpleFilterProvider()
            .addFilter(
                "ignore-getter-errors-filter",
                (PropertyFilter) new IgnoreExceptionBeanPropertyFilter());

    final ObjectMapper mapper = newObjectMapper();
    mapper.enable(
        ORDER_MAP_ENTRIES_BY_KEYS,
        INDENT_OUTPUT,
        USE_EQUALITY_FOR_OBJECT_ID,
        WRITE_ENUMS_USING_TO_STRING);
    mapper.registerModule(new JavaTimeModule());
    mapper.addMixIn(Object.class, JacksonAnnotationMixIn.class);
    mapper.setFilterProvider(filters);
    return mapper;
  }
}

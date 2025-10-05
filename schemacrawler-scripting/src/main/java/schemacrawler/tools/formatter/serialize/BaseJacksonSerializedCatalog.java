/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.formatter.serialize;

import static com.fasterxml.jackson.core.StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;
import static com.fasterxml.jackson.databind.SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_ENUMS_USING_TO_STRING;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.InputStream;
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

  protected static Catalog readCatalog(final InputStream in) {
    requireNonNull(in, "No input stream provided");
    try {
      final ObjectMapper jsonMapper =
          JsonMapper.builder().enable(INCLUDE_SOURCE_IN_LOCATION).build();
      final ObjectMapper mapper = newConfiguredObjectMapper(jsonMapper);
      final Catalog catalog = mapper.readValue(in, Catalog.class);
      return catalog;
    } catch (final IOException e) {
      throw new IORuntimeException("Could not deserialize catalog", e);
    }
  }

  private static ObjectMapper newConfiguredObjectMapper(final ObjectMapper mapper) {
    requireNonNull(mapper, "No object mapper provided");

    @JsonIgnoreProperties({
      "parent",
      "referenced-column",
      "exported-foreign-keys",
      "imported-foreign-keys"
    })
    @JsonPropertyOrder(
        value = {
          "@uuid",
          "@class",
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
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class")
    @JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
    @JsonFilter("ignore-getter-errors-filter")
    class JacksonAnnotationMixIn {}

    final FilterProvider filters =
        new SimpleFilterProvider()
            .addFilter(
                "ignore-getter-errors-filter",
                (PropertyFilter) new IgnoreExceptionBeanPropertyFilter());

    final PolymorphicTypeValidator typeValidator =
        BasicPolymorphicTypeValidator.builder().allowIfSubType(Object.class).build();

    mapper.enable(
        ORDER_MAP_ENTRIES_BY_KEYS,
        INDENT_OUTPUT,
        USE_EQUALITY_FOR_OBJECT_ID,
        WRITE_ENUMS_USING_TO_STRING);
    mapper.registerModule(new JavaTimeModule());
    mapper.addMixIn(Object.class, JacksonAnnotationMixIn.class);
    mapper.setFilterProvider(filters);
    mapper.activateDefaultTyping(typeValidator);
    return mapper;
  }

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
      final ObjectMapper mapper = newConfiguredObjectMapper(newObjectMapper());
      mapper.writeValue(out, this);
      // Jackson will flush and close the stream
    } catch (final IOException e) {
      throw new IORuntimeException("Could not serialize catalog", e);
    }
  }

  protected abstract ObjectMapper newObjectMapper();

  private void loadAllTableColumns() {
    for (final Table table : catalog.getTables()) {
      allTableColumns.addAll(table.getColumns());
    }
  }
}

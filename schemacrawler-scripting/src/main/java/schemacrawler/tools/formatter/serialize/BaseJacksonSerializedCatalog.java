/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.formatter.serialize;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static tools.jackson.core.StreamReadFeature.IGNORE_UNDEFINED;
import static tools.jackson.core.StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION;
import static tools.jackson.core.StreamWriteFeature.IGNORE_UNKNOWN;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
import static tools.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static tools.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;
import static tools.jackson.databind.SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.ser.FilterProvider;
import tools.jackson.databind.ser.PropertyFilter;
import tools.jackson.databind.ser.PropertyWriter;
import tools.jackson.databind.ser.std.SimpleBeanPropertyFilter;
import tools.jackson.databind.ser.std.SimpleFilterProvider;

/** Decorates a database to allow for serialization to and from plain Java serialization. */
public abstract class BaseJacksonSerializedCatalog implements CatalogSerializer {

  private static class IgnoreExceptionBeanPropertyFilter extends SimpleBeanPropertyFilter {

    private static final List<String> PARTIAL_PROPERTIES =
        Arrays.asList(
            "name", "short-name", "full-name", "attributes", "parent-partial", "remarks", "schema");

    @Override
    public void serializeAsProperty(
        Object pojo, JsonGenerator g, SerializationContext provider, PropertyWriter writer)
        throws Exception {
      if (include(writer)) {
        try {
          if (pojo instanceof PartialDatabaseObject
              && !PARTIAL_PROPERTIES.contains(writer.getName())) {
            return;
          }
          writer.serializeAsProperty(pojo, g, provider);
        } catch (final Exception e) {
          LOGGER.log(Level.FINE, e.getMessage(), e);
          return;
        }
      } else if (!g.canOmitProperties()) {
        writer.serializeAsOmittedProperty(pojo, g, provider);
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
      final ObjectMapper mapper = newConfiguredObjectMapper(JsonMapper.builder());
      final Catalog catalog = mapper.readValue(in, Catalog.class);
      return catalog;
    } catch (final JacksonException e) {
      throw new ExecutionRuntimeException("Could not deserialize catalog", e);
    }
  }

  private static ObjectMapper newConfiguredObjectMapper(
      final MapperBuilder<? extends ObjectMapper, ?> mapperBuilder) {

    requireNonNull(mapperBuilder, "No mapper builder provided");
    mapperBuilder.enable(ORDER_MAP_ENTRIES_BY_KEYS, INDENT_OUTPUT, USE_EQUALITY_FOR_OBJECT_ID);
    mapperBuilder.disable(FAIL_ON_NULL_FOR_PRIMITIVES);
    mapperBuilder.enable(INCLUDE_SOURCE_IN_LOCATION, IGNORE_UNDEFINED);
    mapperBuilder.enable(IGNORE_UNKNOWN);

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

    mapperBuilder.addMixIn(Object.class, JacksonAnnotationMixIn.class);
    mapperBuilder.filterProvider(filters);
    mapperBuilder.activateDefaultTyping(typeValidator);

    final ObjectMapper objectMapper = mapperBuilder.build();
    return objectMapper;
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
      final ObjectMapper mapper = newConfiguredObjectMapper(newMapperBuilder());
      mapper.writeValue(out, this);
      // Jackson will flush and close the stream
    } catch (final JacksonException e) {
      throw new ExecutionRuntimeException("Could not serialize catalog", e);
    }
  }

  protected abstract MapperBuilder<? extends ObjectMapper, ?> newMapperBuilder();

  private void loadAllTableColumns() {
    for (final Table table : catalog.getTables()) {
      allTableColumns.addAll(table.getColumns());
    }
  }
}

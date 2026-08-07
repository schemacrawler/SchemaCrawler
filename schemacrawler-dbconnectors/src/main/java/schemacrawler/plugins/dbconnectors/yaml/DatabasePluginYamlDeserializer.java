/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbconnectors.yaml;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.plugins.dbconnectors.model.DatabaseConnectorDefinition;
import schemacrawler.schemacrawler.MetadataRetrievalStrategy;
import schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.dataformat.yaml.YAMLMapper;
import us.fatehi.utility.ioresource.InputResource;
import us.fatehi.utility.string.StringFormat;

/** Parses YAML plugin definitions. */
public final class DatabasePluginYamlDeserializer {

  @JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
  private static record DatabaseConnectorDefinitionHolder(
      DatabaseConnectorDefinition databaseConnector) {}

  private static final Logger LOGGER =
      Logger.getLogger(DatabasePluginYamlDeserializer.class.getName());

  public static final ObjectMapper mapper;

  static {
    final YAMLMapper.Builder yamlMapperBuilder = JsonUtility.newYamlMapperBuilder();
    yamlMapperBuilder.addModule(newEnumModule());
    mapper = yamlMapperBuilder.build();
  }

  private static JacksonModule newEnumModule() {
    final SimpleModule module = new SimpleModule("Retrieval strategy map entries serialization");
    module.addKeySerializer(
        SchemaInfoMetadataRetrievalStrategy.class,
        new ValueSerializer<SchemaInfoMetadataRetrievalStrategy>() {
          @Override
          public void serialize(
              final SchemaInfoMetadataRetrievalStrategy value,
              final JsonGenerator gen,
              final SerializationContext ctxt)
              throws JacksonException {
            gen.writeName(requireNonNull(value, "No retrieval strategy key provided").getKey());
          }
        });
    module.addKeyDeserializer(
        SchemaInfoMetadataRetrievalStrategy.class,
        new KeyDeserializer() {
          @Override
          public Object deserializeKey(final String key, final DeserializationContext ctxt)
              throws JacksonException {
            return SchemaInfoMetadataRetrievalStrategy.valueOfFromKey(key);
          }
        });
    module.addSerializer(
        MetadataRetrievalStrategy.class,
        new ValueSerializer<MetadataRetrievalStrategy>() {
          @Override
          public void serialize(
              final MetadataRetrievalStrategy value,
              final JsonGenerator gen,
              final SerializationContext ctxt)
              throws JacksonException {
            if (value == null) {
              gen.writeNull();
            } else {
              gen.writeString(value.name().replace('_', '-'));
            }
          }
        });
    module.addDeserializer(
        MetadataRetrievalStrategy.class,
        new ValueDeserializer<MetadataRetrievalStrategy>() {
          @Override
          public MetadataRetrievalStrategy deserialize(
              final JsonParser p, final DeserializationContext ctxt) throws JacksonException {
            final String key = p.getString();
            if (isBlank(key)) {
              return MetadataRetrievalStrategy.none;
            }
            return MetadataRetrievalStrategy.valueOf(key.replace('-', '_'));
          }
        });
    return module;
  }

  public DatabaseConnectorDefinition parse(final InputResource inputResource) {
    requireNonNull(inputResource, "No input resource provided");
    LOGGER.log(Level.FINE, new StringFormat("Parsing <%s>", inputResource));
    try (final Reader reader = inputResource.openNewInputReader(UTF_8)) {
      try {
        final DatabaseConnectorDefinitionHolder definition =
            mapper.readerFor(DatabaseConnectorDefinitionHolder.class).readValue(reader);
        return definition.databaseConnector();
      } catch (final JacksonException e) {
        e.prependPath(new DatabindException.Reference(null, inputResource.toString()));
        throw e;
      }
    } catch (final Exception e) {
      LOGGER.log(
          Level.WARNING,
          "Could not read database connector definition from <%s>".formatted(inputResource),
          e);
      return new DatabaseConnectorDefinition();
    }
  }
}

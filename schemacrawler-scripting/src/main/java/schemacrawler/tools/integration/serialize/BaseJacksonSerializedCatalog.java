/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.integration.serialize;


import static com.fasterxml.jackson.databind.SerializationFeature.*;
import static java.util.Objects.requireNonNull;

import java.io.OutputStream;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.BaseCatalogDecorator;
import schemacrawler.schemacrawler.SchemaCrawlerException;

/**
 * Decorates a database to allow for serialization to and from plain
 * Java serialization.
 */
public abstract class BaseJacksonSerializedCatalog
  extends BaseCatalogDecorator
  implements SerializableCatalog
{

  private static final long serialVersionUID = 5314326260124511414L;

  private final SortedSet<Column> allTableColumns;

  public BaseJacksonSerializedCatalog(final Catalog catalog)
  {
    super(catalog);
    allTableColumns = new TreeSet<>();
    loadAllTableColumns();
  }

  public Set<Column> getAllTableColumns()
  {
    return new TreeSet<>(allTableColumns);
  }

  /**
   * Serializes the database to the writer, as JSON.
   *
   * @param out Output stream to save to
   * @throws SchemaCrawlerException On an exception
   */
  @Override
  public void save(final OutputStream out)
    throws SchemaCrawlerException
  {
    requireNonNull(out, "No output stream provided");
    try
    {

      @JsonIgnoreProperties({
                              "parent",
                              "referenced-column",
                              "exported-foreign-keys",
                              "imported-foreign-keys" })
      @JsonPropertyOrder(value = {
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
        "all-table-columns" }, alphabetic = true)
      @JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property = "@uuid")
      @JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
      class JacksonAnnotationMixIn
      {

      }

      final ObjectMapper mapper = newObjectMapper();
      mapper.enable(ORDER_MAP_ENTRIES_BY_KEYS,
                    INDENT_OUTPUT,
                    USE_EQUALITY_FOR_OBJECT_ID,
                    WRITE_ENUMS_USING_TO_STRING);
      mapper.addMixIn(Object.class, JacksonAnnotationMixIn.class);

      // Write JSON to stream
      mapper.writeValue(out, this);
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Could not serialize catalog", e);
    }
  }

  protected abstract ObjectMapper newObjectMapper();

  private void loadAllTableColumns()
  {
    for (final Table table : catalog.getTables())
    {
      for (final Column column : table.getColumns())
      {
        allTableColumns.add(column);
      }
    }
  }

}

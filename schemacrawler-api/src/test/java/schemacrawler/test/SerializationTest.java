/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
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

package schemacrawler.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseDatabaseTest;

public class SerializationTest
  extends BaseDatabaseTest
{

  @Test
  public void catalogSerialization()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());

    final Catalog catalog = getCatalog(schemaCrawlerOptions);
    assertNotNull("Could not obtain catalog", catalog);
    assertTrue("Could not find any schemas", catalog.getSchemas().size() > 0);

    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").orElse(null);
    assertNotNull("Could not obtain schema", schema);
    assertEquals("Unexpected number of tables in the schema",
                 6,
                 catalog.getTables(schema).size());

    final Catalog clonedCatalog = SerializationUtils.clone(catalog);

    assertEquals(catalog, clonedCatalog);

    assertNotNull("Could not obtain catalog", clonedCatalog);
    assertTrue("Could not find any schemas",
               clonedCatalog.getSchemas().size() > 0);

    final Schema clonedSchema = clonedCatalog.lookupSchema("PUBLIC.BOOKS")
      .orElse(null);
    assertNotNull("Could not obtain schema", clonedSchema);
    assertEquals("Unexpected number of tables in the schema",
                 6,
                 clonedCatalog.getTables(clonedSchema).size());
  }

}

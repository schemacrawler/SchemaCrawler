/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.integration.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.BaseDatabaseTest;
import sf.util.IOUtility;

public class CatalogSerializationTest
  extends BaseDatabaseTest
{

  @Test
  public void catalogSerializationWithFst()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = SchemaCrawlerOptionsBuilder
      .withMaximumSchemaInfoLevel();

    final Catalog catalog = getCatalog(schemaCrawlerOptions);
    assertNotNull("Could not obtain catalog", catalog);
    assertTrue("Could not find any schemas", catalog.getSchemas().size() > 0);

    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").orElse(null);
    assertNotNull("Could not obtain schema", schema);
    assertEquals("Unexpected number of tables in the schema",
                 10,
                 catalog.getTables(schema).size());

    final Path testOutputFile = IOUtility
      .createTempFilePath("sc_fst_serialization", "fst");
    try (
        final FSTObjectOutput fstout = new FSTObjectOutput(new FileOutputStream(testOutputFile
          .toFile()));)
    {
      fstout.writeObject(catalog);
    }
    assertTrue("Catalog was not serialized", Files.size(testOutputFile) > 0);

    Catalog catalogDeserialized = null;
    try (
        final FSTObjectInput fstin = new FSTObjectInput(new FileInputStream(testOutputFile
          .toFile()));)
    {
      catalogDeserialized = (Catalog) fstin.readObject();
    }

    final Schema schemaDeserialized = catalogDeserialized
      .lookupSchema("PUBLIC.BOOKS").orElse(null);
    assertNotNull("Could not obtain schema", schemaDeserialized);
    assertEquals("Unexpected number of tables in the schema",
                 10,
                 catalogDeserialized.getTables(schemaDeserialized).size());
  }

  @Test
  public void catalogSerializationWithJava()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = SchemaCrawlerOptionsBuilder
      .withMaximumSchemaInfoLevel();

    final Catalog catalog = getCatalog(schemaCrawlerOptions);
    assertNotNull("Could not obtain catalog", catalog);
    assertTrue("Could not find any schemas", catalog.getSchemas().size() > 0);

    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").orElse(null);
    assertNotNull("Could not obtain schema", schema);
    assertEquals("Unexpected number of tables in the schema",
                 10,
                 catalog.getTables(schema).size());

    final Path testOutputFile = IOUtility
      .createTempFilePath("sc_java_serialization", "ser");
    try (
        final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(testOutputFile
          .toFile()));)
    {
      out.writeObject(catalog);
    }
    assertTrue("Catalog was not serialized", Files.size(testOutputFile) > 0);

    Catalog catalogDeserialized = null;
    try (
        final ObjectInputStream in = new ObjectInputStream(new FileInputStream(testOutputFile
          .toFile()));)
    {
      catalogDeserialized = (Catalog) in.readObject();
    }

    final Schema schemaDeserialized = catalogDeserialized
      .lookupSchema("PUBLIC.BOOKS").orElse(null);
    assertNotNull("Could not obtain schema", schemaDeserialized);
    assertEquals("Unexpected number of tables in the schema",
                 10,
                 catalogDeserialized.getTables(schemaDeserialized).size());
  }

}

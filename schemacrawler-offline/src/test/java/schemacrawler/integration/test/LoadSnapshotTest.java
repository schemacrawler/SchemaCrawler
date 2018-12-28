/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.nio.file.Files.size;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.tools.integration.serialization.XmlSerializedCatalog;
import sf.util.IOUtility;

public class LoadSnapshotTest
  extends BaseDatabaseTest
{

  private Path serializedCatalogFile;

  @Test
  public void loadSnapshot()
    throws Exception
  {
    final FileInputStream inputFileStream = new FileInputStream(serializedCatalogFile
      .toFile());
    final XmlSerializedCatalog serializedCatalog = new XmlSerializedCatalog(inputFileStream);

    final Schema schema = serializedCatalog.lookupSchema("PUBLIC.BOOKS")
      .orElse(null);
    assertNotNull("Could not obtain schema", schema);
    assertEquals("Unexpected number of tables in the schema",
                 10,
                 serializedCatalog.getTables(schema).size());
  }

  @Before
  public void serializeCatalog()
    throws SchemaCrawlerException, IOException
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsWithMaximumSchemaInfoLevel();

    final Catalog catalog = getCatalog(schemaCrawlerOptions);
    assertNotNull("Could not obtain catalog", catalog);
    assertTrue("Could not find any schemas", catalog.getSchemas().size() > 0);

    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").orElse(null);
    assertNotNull("Could not obtain schema", schema);
    assertEquals("Unexpected number of tables in the schema",
                 10,
                 catalog.getTables(schema).size());

    serializedCatalogFile = IOUtility.createTempFilePath("schemacrawler",
                                                         "ser");

    final XmlSerializedCatalog serializedCatalog = new XmlSerializedCatalog(catalog);
    serializedCatalog
      .save(new FileOutputStream(serializedCatalogFile.toFile()));
    assertNotSame("Database was not serialized",
                  0,
                  size(serializedCatalogFile));

  }

}

/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.integration.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.FileWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.tools.integration.serialization.XmlDatabase;
import schemacrawler.utility.TestDatabase;

public class SchemaSerializationTest
{

  private static TestDatabase testDatabase = new TestDatabase();

  @AfterClass
  public static void afterAllTests()
  {
    testDatabase.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
    throws Exception
  {
    TestDatabase.initializeApplicationLogging();
    testDatabase.startMemoryDatabase();
  }

  @Test
  public void schemaSerializationWithXStream()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());

    final Database database = testDatabase.getDatabase(schemaCrawlerOptions);
    assertNotNull("Could not obtain database", database);
    assertTrue("Could not find any schemas", database.getSchemas().length > 0);

    final Schema schema = database.getSchema("PUBLIC.BOOKS");
    assertNotNull("Could not obtain schema", schema);
    assertEquals("Unexpected number of tables in the schema",
                 6,
                 schema.getTables().length);

    XmlDatabase xmlDatabase;
    StringWriter writer;

    xmlDatabase = new XmlDatabase(database);
    writer = new StringWriter();
    xmlDatabase.save(writer);
    writer.close();
    final String xmlSerializedCatalog1 = writer.toString();
    assertNotNull("Catalog was not serialized to XML", xmlSerializedCatalog1);
    assertNotSame("Catalog was not serialized to XML", 0, xmlSerializedCatalog1
      .trim().length());

    xmlDatabase = new XmlDatabase(new StringReader(xmlSerializedCatalog1));
    final Database deserializedDatabase = xmlDatabase;
    assertNotNull("No database deserialized", deserializedDatabase);
    final Schema deserializedSchema = deserializedDatabase
      .getSchema("PUBLIC.BOOKS");
    assertNotNull("Could not obtain deserialized schema", deserializedSchema);
    assertEquals("Unexpected number of tables in the deserialized schema",
                 6,
                 deserializedSchema.getTables().length);

    writer = new StringWriter();
    xmlDatabase.save(writer);
    writer.close();
    final String xmlSerializedCatalog2 = writer.toString();
    assertNotNull("Catalog was not serialized to XML", xmlSerializedCatalog2);
    assertNotSame("Catalog was not serialized to XML", 0, xmlSerializedCatalog2
      .trim().length());

    final DetailedDiff xmlDiff = new DetailedDiff(new Diff(xmlSerializedCatalog1,
                                                           xmlSerializedCatalog2));
    final List<?> allDifferences = xmlDiff.getAllDifferences();
    if (!xmlDiff.similar())
    {
      IOUtils.write(xmlSerializedCatalog1,
                    new FileWriter("serialized-schema-1.xml"));
      IOUtils.write(xmlSerializedCatalog2,
                    new FileWriter("serialized-schema-2.xml"));
    }
    assertEquals(xmlDiff.toString(), 0, allDifferences.size());
  }

}

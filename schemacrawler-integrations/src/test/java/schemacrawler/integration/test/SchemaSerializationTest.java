/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.junit.Test;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.tools.integration.serialization.XmlSerializedCatalog;

public class SchemaSerializationTest
  extends BaseDatabaseTest
{

  @Test
  public void schemaSerializationWithXStream()
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

    XmlSerializedCatalog xmlCatalog;
    StringWriter writer;

    xmlCatalog = new XmlSerializedCatalog(catalog);
    writer = new StringWriter();
    xmlCatalog.save(writer);
    writer.close();
    final String xmlSerializedCatalog1 = writer.toString();
    assertNotNull("Catalog was not serialized to XML", xmlSerializedCatalog1);
    assertNotSame("Catalog was not serialized to XML",
                  0,
                  xmlSerializedCatalog1.trim().length());

    xmlCatalog = new XmlSerializedCatalog(new StringReader(xmlSerializedCatalog1));
    final Catalog deserializedCatalog = xmlCatalog;
    assertNotNull("No database deserialized", deserializedCatalog);
    final Schema deserializedSchema = deserializedCatalog
      .lookupSchema("PUBLIC.BOOKS").orElse(null);
    assertNotNull("Could not obtain deserialized schema", deserializedSchema);
    assertEquals("Unexpected number of tables in the deserialized schema",
                 6,
                 catalog.getTables(deserializedSchema).size());

    writer = new StringWriter();
    xmlCatalog.save(writer);
    writer.close();
    final String xmlSerializedCatalog2 = writer.toString();
    assertNotNull("Catalog was not serialized to XML", xmlSerializedCatalog2);
    assertNotSame("Catalog was not serialized to XML",
                  0,
                  xmlSerializedCatalog2.trim().length());

    final DetailedDiff xmlDiff = new DetailedDiff(new Diff(xmlSerializedCatalog1,
                                                           xmlSerializedCatalog2));
    final List<?> allDifferences = xmlDiff.getAllDifferences();
    if (!xmlDiff.similar())
    {
      IOUtils.write(xmlSerializedCatalog1,
                    new PrintWriter("serialized-schema-1.xml", UTF_8.name()));
      IOUtils.write(xmlSerializedCatalog2,
                    new PrintWriter("serialized-schema-2.xml", UTF_8.name()));
    }
    assertEquals(xmlDiff.toString(), 0, allDifferences.size());
  }

}

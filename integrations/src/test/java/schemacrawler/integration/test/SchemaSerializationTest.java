/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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

import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.tools.integration.xml.XmlSchemaCrawler;
import schemacrawler.utility.datasource.PropertiesDataSourceException;
import schemacrawler.utility.test.TestUtility;

import com.thoughtworks.xstream.XStream;

public class SchemaSerializationTest
{
  private static TestUtility testUtility = new TestUtility();

  @AfterClass
  public static void afterAllTests()
    throws PropertiesDataSourceException, ClassNotFoundException
  {
    testUtility.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
    throws PropertiesDataSourceException, ClassNotFoundException
  {
    TestUtility.setApplicationLogLevel();
    testUtility.createMemoryDatabase();
  }

  @Test
  public void schemaSerializationWithXmlSchemaCrawler()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setShowStoredProcedures(true);
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());

    final Schema schema = testUtility.getSchema(schemaCrawlerOptions);
    assertNotNull("No schema provided", schema);
    assertEquals("Unexpected number of tables in the schema", 6, schema
      .getTables().length);

    XmlSchemaCrawler xmlSchemaCrawler;
    StringWriter writer;

    xmlSchemaCrawler = new XmlSchemaCrawler(schema);
    writer = new StringWriter();
    xmlSchemaCrawler.save(writer);
    writer.close();
    final String xmlSerializedSchema1 = writer.toString();
    assertNotNull("Schema was not serialized to XML", xmlSerializedSchema1);
    assertNotSame("Schema was not serialized to XML", 0, xmlSerializedSchema1
      .trim().length());

    xmlSchemaCrawler = new XmlSchemaCrawler(new StringReader(xmlSerializedSchema1));
    writer = new StringWriter();
    xmlSchemaCrawler.save(writer);
    writer.close();
    final String xmlSerializedSchema2 = writer.toString();
    assertNotNull("Schema was not serialized to XML", xmlSerializedSchema2);
    assertNotSame("Schema was not serialized to XML", 0, xmlSerializedSchema2
      .trim().length());

    final DetailedDiff myDiff = new DetailedDiff(new Diff(xmlSerializedSchema1,
                                                          xmlSerializedSchema2));
    final List<?> allDifferences = myDiff.getAllDifferences();
    if (!myDiff.similar())
    {
      IOUtils.write(xmlSerializedSchema1,
                    new FileWriter("/temp/serialized-schema-1.xml"));
      IOUtils.write(xmlSerializedSchema2,
                    new FileWriter("/temp/serialized-schema-2.xml"));
    }
    assertEquals(myDiff.toString(), 0, allDifferences.size());
  }

  @Test
  public void schemaSerializationWithXStream()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setShowStoredProcedures(true);
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());

    final Schema schema = testUtility.getSchema(schemaCrawlerOptions);
    assertNotNull("No schema provided", schema);
    assertEquals("Unexpected number of tables in the schema", 6, schema
      .getTables().length);

    final XStream xStream = new XStream();

    final String xmlSerializedSchema1 = xStream.toXML(schema);
    assertNotNull("Schema was not serialized to XML", xmlSerializedSchema1);
    assertNotSame("Schema was not serialized to XML", 0, xmlSerializedSchema1
      .trim().length());

    final Schema deserializedSchema = (Schema) xStream
      .fromXML(xmlSerializedSchema1);
    final String xmlSerializedSchema2 = xStream.toXML(deserializedSchema);
    assertNotNull("Schema was not serialized to XML", xmlSerializedSchema2);
    assertNotSame("Schema was not serialized to XML", 0, xmlSerializedSchema2
      .trim().length());

    final DetailedDiff myDiff = new DetailedDiff(new Diff(xmlSerializedSchema1,
                                                          xmlSerializedSchema2));
    final List<?> allDifferences = myDiff.getAllDifferences();
    if (!myDiff.similar())
    {
      IOUtils.write(xmlSerializedSchema1,
                    new FileWriter("/temp/serialized-schema-1.xml"));
      IOUtils.write(xmlSerializedSchema2,
                    new FileWriter("/temp/serialized-schema-2.xml"));
    }
    assertEquals(myDiff.toString(), 0, allDifferences.size());
  }

}

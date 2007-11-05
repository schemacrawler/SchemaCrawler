/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.crawl.SchemaInfoLevel;
import schemacrawler.schema.Schema;

import com.thoughtworks.xstream.XStream;

import dbconnector.datasource.PropertiesDataSourceException;
import dbconnector.test.TestUtility;

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
    testUtility.setApplicationLogLevel();
    testUtility.createMemoryDatabase();
  }

  @Test
  public void schemaSerializationWithXStream()
    throws Exception
  {
    final SchemaCrawlerOptions options = new SchemaCrawlerOptions();
    options.setShowStoredProcedures(true);

    final Schema schema = SchemaCrawler.getSchema(testUtility.getDataSource(),
                                                  SchemaInfoLevel.maximum,
                                                  options);
    final XStream xStream = new XStream();

    final String xmlSerializedSchema1 = xStream.toXML(schema);

    final Schema deserializedSchema = (Schema) xStream
      .fromXML(xmlSerializedSchema1);
    final String xmlSerializedSchema2 = xStream.toXML(deserializedSchema);

    final DetailedDiff myDiff = new DetailedDiff(new Diff(xmlSerializedSchema1,
                                                          xmlSerializedSchema2));
    final List<?> allDifferences = myDiff.getAllDifferences();
    if (!myDiff.similar())
    {
      write(xmlSerializedSchema1, "/temp/serialized-schema-1.xml");
      write(xmlSerializedSchema2, "/temp/serialized-schema-2.xml");
    }
    assertEquals(myDiff.toString(), 0, allDifferences.size());
  }

  private void write(final String contents, final String filename)
  {
    Writer writer = null;
    try
    {
      final File file = new File(filename);
      file.getCanonicalFile().getParentFile().mkdirs();
      writer = new FileWriter(file);
      writer.write(contents);
      writer.flush();
    }
    catch (final IOException e)
    {
      e.printStackTrace();
    }
    finally
    {
      if (writer != null)
      {
        try
        {
          writer.close();
        }
        catch (IOException e)
        {
          // Ignore
        }
      }
    }
  }

}

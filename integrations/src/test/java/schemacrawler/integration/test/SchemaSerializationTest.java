/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import junit.framework.Test;
import junit.framework.TestSuite;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.crawl.SchemaInfoLevel;
import schemacrawler.schema.Schema;

import com.thoughtworks.xstream.XStream;

import dbconnector.test.util.TestBase;

public class SchemaSerializationTest
  extends TestBase
{

  private static final int CONTEXT = 50;

  public static Test suite()
  {
    return new TestSuite(SchemaSerializationTest.class);
  }

  public SchemaSerializationTest(final String name)
  {
    super(name);
  }

  public void testSchemaSerializationWithXStream()
  {
    final SchemaCrawlerOptions options = new SchemaCrawlerOptions();
    options.setShowStoredProcedures(true);

    final Schema schema = SchemaCrawler.getSchema(dataSource, null,
                                                  SchemaInfoLevel.MAXIMUM,
                                                  options);
    final XStream xStream = new XStream();

    final String xmlSerializedSchema1 = xStream.toXML(schema);

    final Schema deserializedSchema = (Schema) xStream
      .fromXML(xmlSerializedSchema1);
    final String xmlSerializedSchema2 = xStream.toXML(deserializedSchema);

    if (!checkXml(xmlSerializedSchema1, xmlSerializedSchema2))
    {
      write(xmlSerializedSchema1, "/temp/serialized-schema-1.xml");
      write(xmlSerializedSchema2, "/temp/serialized-schema-2.xml");
      fail();
    }
  }

  private boolean checkXml(final String expectedXml, final String actualXml)
  {
    if (!actualXml.equals(expectedXml))
    {
      int index;
      final int minLength = Math.min(expectedXml.length(), actualXml.length());
      for (index = 0; index <= minLength; ++index)
      {
        if (expectedXml.charAt(index) != actualXml.charAt(index))
        {
          break;
        }
      }
      if (index <= minLength)
      {
        final int expectedStartPos = Math.max(0, (index - CONTEXT));
        final int expectedEndPos = Math.min((index + CONTEXT), expectedXml.length());
        final int actualStartPos = Math.max(0, (index - CONTEXT));
        final int actualEndPos = Math.min((index + CONTEXT), actualXml.length());
        System.out.println("expected: "
                           + expectedXml.substring(expectedStartPos,
                                                   expectedEndPos));
        System.out.println("actual: "
                           + actualXml.substring(actualStartPos, actualEndPos));
      }
      return false;
    }
    else
    {
      return true;
    }
  }

  private void write(final String contents, final String filename)
  {
    try
    {
      final File file = new File(filename);
      file.getAbsoluteFile().getParentFile().mkdirs();
      final Writer writer = new FileWriter(file);
      writer.write(contents);
      writer.close();
    }
    catch (final IOException e)
    {
      e.printStackTrace();
    }

  }
}

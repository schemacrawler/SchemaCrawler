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
package schemacrawler.test.utility;


import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class LocalEntityResolver
  implements EntityResolver
{

  @Override
  public InputSource resolveEntity(final String publicId, final String systemId)
    throws SAXException, IOException
  {
    final String localResource = "/xhtml1" + systemId
      .substring(systemId.lastIndexOf('/'));
    final InputStream entityStream = LocalEntityResolver.class
      .getResourceAsStream(localResource);
    if (entityStream == null)
    {
      throw new IOException("Could not load " + localResource);
    }
    return new InputSource(entityStream);
  }

}

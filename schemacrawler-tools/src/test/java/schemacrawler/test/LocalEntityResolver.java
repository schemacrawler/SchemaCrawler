package schemacrawler.test;


import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class LocalEntityResolver
  implements EntityResolver
{

  public InputSource resolveEntity(final String publicId, final String systemId)
    throws SAXException, IOException
  {
    final String localResource = "/xhtml1"
                                 + systemId
                                   .substring(systemId.lastIndexOf('/'));
    final InputStream entityStream = LocalEntityResolver.class
      .getResourceAsStream(localResource);
    if (entityStream == null)
    {
      final String message = "Could not load " + localResource;
      System.err.println(message);
      throw new IOException(message);
    }
    return new InputSource(entityStream);
  }

}

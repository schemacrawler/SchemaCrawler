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
package schemacrawler.tools.integration.serialization;


import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.BaseCatalogDecorator;
import schemacrawler.schemacrawler.SchemaCrawlerException;

/**
 * Decorates a database to allow for serialization to and from XML.
 *
 * @author sfatehi
 */
public final class XmlSerializedCatalog
  extends BaseCatalogDecorator
  implements SerializableCatalog
{

  private static final long serialVersionUID = 5314326260124511414L;

  private static Catalog readCatalog(final InputStream in)
    throws SchemaCrawlerException
  {
    requireNonNull(in, "No input stream provided");
    try (final FSTObjectInput fstin = new FSTObjectInput(in);)
    {
      return (Catalog) fstin.readObject();
    }
    catch (ClassNotFoundException | IOException e)
    {
      throw new SchemaCrawlerException("Cannot deserialize catalog", e);
    }
  }

  public XmlSerializedCatalog(final Catalog catalog)
  {
    super(catalog);
  }

  public XmlSerializedCatalog(final InputStream in)
    throws SchemaCrawlerException
  {
    this(readCatalog(in));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final <T> T getAttribute(final String name)
  {
    return getAttribute(name, (T) null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final <T> Optional<T> lookupAttribute(final String name)
  {
    return Optional.of(getAttribute(name));
  }

  /**
   * Serializes the database to the writer, as XML.
   *
   * @param out
   *        Writer to save to
   * @throws SchemaCrawlerException
   *         On an exception
   */
  @Override
  public void save(final OutputStream out)
    throws SchemaCrawlerException
  {
    requireNonNull(out, "No output stream provided");
    try (final FSTObjectOutput fstout = new FSTObjectOutput(out);)
    {
      fstout.writeObject(catalog);
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerException("Could not serialize catalog", e);
    }
  }

}

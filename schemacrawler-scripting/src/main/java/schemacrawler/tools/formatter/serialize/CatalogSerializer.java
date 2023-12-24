/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.formatter.serialize;

import java.io.OutputStream;
import java.io.Writer;

import schemacrawler.schema.Catalog;

public interface CatalogSerializer {

  /**
   * Gets the catalog wrapped by ths savable.
   *
   * @return Catalog
   */
  Catalog getCatalog();

  /**
   * Serialize catalog to a binary stream. If the serialization format is text-based, specified
   * character encoding will not be honored.
   *
   * @param out Output stream
   */
  void save(final OutputStream out);

  /**
   * Serialize catalog to a binary stream. If the serialization format is text-based, specified
   * character encoding will be honored. If the serialization format is binary, and exception will
   * be thrown.
   *
   * @param out Output stream
   */
  void save(final Writer out);
}

/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.command.chatgpt.systemfunctions;

import static java.util.Objects.requireNonNull;
import schemacrawler.tools.command.chatgpt.FunctionReturn;
import schemacrawler.tools.command.serialize.model.CatalogDescription;

public class SchemaFunctionReturn implements FunctionReturn {

  private final CatalogDescription catalogDescription;

  protected SchemaFunctionReturn(final CatalogDescription catalogDescription) {
    this.catalogDescription = requireNonNull(catalogDescription, "No catalog provided");
  }

  @Override
  public String get() {
    return String.format("Description of database schema in JSON:%n%s", catalogDescription);
  }
}

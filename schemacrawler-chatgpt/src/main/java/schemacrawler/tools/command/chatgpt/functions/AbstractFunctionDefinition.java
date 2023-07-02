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

package schemacrawler.tools.command.chatgpt.functions;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;
import java.util.Objects;
import schemacrawler.schema.Catalog;

public abstract class AbstractFunctionDefinition<P extends FunctionParameters>
    implements FunctionDefinition<P> {

  private final String name;
  private final String description;
  private final Class<P> parameters;
  protected Catalog catalog;

  protected AbstractFunctionDefinition(
      final String name, final String description, final Class<P> parameters) {
    this.name = requireNotBlank(name, "Function name not provided");
    this.description = requireNotBlank(description, "Function description not provided");
    this.parameters = requireNonNull(parameters, "Function parameters not provided");
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final AbstractFunctionDefinition<?> other = (AbstractFunctionDefinition<?>) obj;
    return Objects.equals(description, other.description)
        && Objects.equals(name, other.name)
        && Objects.equals(parameters, other.parameters);
  }

  @Override
  public Catalog getCatalog() {
    return catalog;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Class<P> getParameters() {
    return parameters;
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, name, parameters);
  }

  @Override
  public void setCatalog(final Catalog catalog) {
    this.catalog = catalog;
  }

  @Override
  public String toString() {
    return String.format("function %s(%s)%n\"%s\"", name, parameters.getSimpleName(), description);
  }
}

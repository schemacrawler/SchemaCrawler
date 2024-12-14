/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.executable;

import java.sql.Connection;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;

/** A SchemaCrawler tools executable unit. */
public abstract class BaseCommand<C, R> implements Command<C, R> {

  protected final String command;
  protected C commandOptions;
  protected Catalog catalog;
  protected Connection connection;

  protected BaseCommand(final String command) {
    this.command = requireNotBlank(command, "No command specified");
  }

  @Override
  public void configure(final C commandOptions) {
    this.commandOptions = requireNonNull(commandOptions, "No command options provided");
  }

  @Override
  public final Catalog getCatalog() {
    return catalog;
  }

  @Override
  public final Connection getConnection() {
    return connection;
  }

  @Override
  public final String getName() {
    return command;
  }

  @Override
  public void initialize() {
    // No-op by default
  }

  @Override
  public final void setCatalog(final Catalog catalog) {
    this.catalog = requireNonNull(catalog, "No catalog provided");
  }

  @Override
  public final void setConnection(final Connection connection) {
    if (!usesConnection()) {
      throw new ExecutionRuntimeException(
          String.format("<%s> does not use a connection", getName()));
    }
    this.connection = connection;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return command;
  }
}

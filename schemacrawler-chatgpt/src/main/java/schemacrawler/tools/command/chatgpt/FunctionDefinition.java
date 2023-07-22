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

package schemacrawler.tools.command.chatgpt;

import static schemacrawler.tools.command.chatgpt.FunctionDefinition.FunctionType.USER;
import java.sql.Connection;
import java.util.function.Function;
import schemacrawler.schema.Catalog;

public interface FunctionDefinition<P extends FunctionParameters> {

  public enum FunctionType {
    USER,
    SYSTEM;
  }

  Catalog getCatalog();

  String getDescription();

  Function<P, FunctionReturn> getExecutor();

  default FunctionType getFunctionType() {
    return USER;
  }

  default String getName() {
    return this.getClass().getSimpleName();
  }

  Class<P> getParameters();

  void setCatalog(Catalog catalog);

  void setConnection(Connection connection);
}

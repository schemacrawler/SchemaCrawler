/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.util.Objects.requireNonNull;

import schemacrawler.tools.text.operation.Operation;

final class OperationExecutableCommandProvider
  extends ExecutableCommandProvider
{

  private static final String OPERATION_EXECUTABLE = "schemacrawler.tools.text.operation.OperationExecutable";

  OperationExecutableCommandProvider(final Operation operation)
  {
    super(requireNonNull(operation, "No operation provided").name(),
          OPERATION_EXECUTABLE);
  }

  OperationExecutableCommandProvider(final String operation)
  {
    super(requireNonNull(operation, "No operation provided"),
          OPERATION_EXECUTABLE);
  }

}

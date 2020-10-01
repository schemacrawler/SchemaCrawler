/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.text.operation;

import static java.util.Objects.requireNonNull;

import schemacrawler.schemacrawler.Query;
import schemacrawler.tools.text.base.BaseTextOptions;

/**
 * Operator options.
 *
 * @author Sualeh Fatehi
 */
public final class OperationOptions extends BaseTextOptions {

  private final Operation operation;
  private final Query query;
  private final boolean isShowLobs;

  protected OperationOptions(final OperationOptionsBuilder builder) {
    super(builder);

    operation = builder.operation;
    if (builder.operation == null) {
      query = requireNonNull(builder.query, "No query provided");
    } else {
      query = operation.getQuery();
    }

    isShowLobs = builder.isShowLobs;
  }

  public Operation getOperation() {
    return operation;
  }

  public Query getQuery() {
    return query;
  }

  /**
   * Whether to show LOBs.
   *
   * @return Whether to show LOBs.
   */
  public boolean isShowLobs() {
    return isShowLobs;
  }
}

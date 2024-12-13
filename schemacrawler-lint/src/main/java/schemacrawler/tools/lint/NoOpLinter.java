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

package schemacrawler.tools.lint;

import java.sql.Connection;
import schemacrawler.schema.Table;
import us.fatehi.utility.property.PropertyName;

public final class NoOpLinter extends BaseLinter {

  NoOpLinter() {
    super(new PropertyName("schemacrawler.NO_OP_LINTER", ""), new LintCollector());
  }

  @Override
  public String getSummary() {
    return "No-op linter";
  }

  @Override
  protected void lint(final Table table, final Connection connection) {
    // No-op
  }
}

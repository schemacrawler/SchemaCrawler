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
package schemacrawler.loader.weakassociations;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import schemacrawler.schema.Column;

public final class IdMatcher implements Predicate<ProposedWeakAssociation> {

  private static final Pattern endsWithIdPattern = Pattern.compile(".*(?i)_?id$");
  private static final Pattern isIdPattern = Pattern.compile("^(?i)_?id$");

  @Override
  public boolean test(final ProposedWeakAssociation proposedWeakAssociation) {
    if (proposedWeakAssociation == null) {
      return false;
    }

    final Column foreignKeyColumn = proposedWeakAssociation.getForeignKeyColumn();
    final Column primaryKeyColumn = proposedWeakAssociation.getPrimaryKeyColumn();

    final boolean fkColEndsWithId = endsWithIdPattern.matcher(foreignKeyColumn.getName()).matches();
    final boolean pkColEndsWithId =
        endsWithIdPattern.matcher(primaryKeyColumn.getName()).matches()
            && !isIdPattern.matcher(primaryKeyColumn.getName()).matches();

    return fkColEndsWithId && !pkColEndsWithId;
  }
}

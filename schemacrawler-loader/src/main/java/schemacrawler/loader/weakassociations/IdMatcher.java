/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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

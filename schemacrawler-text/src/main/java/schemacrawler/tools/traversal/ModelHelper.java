/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.traversal;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.ermodel.model.Relationship;
import schemacrawler.ermodel.model.RelationshipCardinality;
import schemacrawler.ermodel.utility.ERModelUtility;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableReference;
import schemacrawler.tools.state.ExecutionState;

public final class ModelHelper {

  public static ModelHelper from(final ExecutionState state) {
    requireNonNull(state, "No execution state provided");
    final ERModel erModel;
    if (state.hasERModel()) {
      erModel = state.getERModel();
    } else {
      erModel = ERModelUtility.buildEmptyERModel();
    }
    return new ModelHelper(erModel);
  }

  private final ERModel erModel;

  private ModelHelper(final ERModel erModel) {
    this.erModel = requireNonNull(erModel, "No ER model provided");
  }

  public Collection<? extends TableReference> getImplicitAssociations(final Table table) {
    final Collection<? extends TableReference> implicitAssociations =
        ERModelUtility.collectImplicitAssociations(table, erModel);
    return List.copyOf(implicitAssociations);
  }

  public RelationshipCardinality inferCardinality(final TableReference tableReference) {
    if (tableReference == null) {
      return RelationshipCardinality.unknown;
    }

    final Optional<Relationship> lookedupRelationship = erModel.lookupRelationship(tableReference);
    if (lookedupRelationship.isEmpty()) {
      // Relationship may belong to a many-to-many relationship, so compute the
      // cardinality
      return ERModelUtility.inferCardinality(tableReference);
    }
    final RelationshipCardinality cardinality = lookedupRelationship.get().getType();
    return cardinality;
  }
}

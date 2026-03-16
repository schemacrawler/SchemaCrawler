/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.traversal;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.ermodel.model.Relationship;
import schemacrawler.ermodel.model.RelationshipCardinality;
import schemacrawler.ermodel.utility.EntityModelUtility;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.TableReference;
import schemacrawler.tools.state.ExecutionState;

public final class ModelHelper {

  public static ModelHelper from(final ExecutionState state) {
    requireNonNull(state, "No execution state provided");
    return new ModelHelper(state.getCatalog(), state.getERModel());
  }

  private final ERModel erModel;

  private ModelHelper(final Catalog catalog, final ERModel erModel) {
    this.erModel = erModel;
  }

  public RelationshipCardinality inferCardinality(final TableReference tableReference) {
    if (tableReference == null) {
      return RelationshipCardinality.unknown;
    }
    if (erModel == null) {
      return EntityModelUtility.inferCardinality(tableReference);
    }

    final Optional<Relationship> lookedupRelationship = erModel.lookupRelationship(tableReference);
    if (lookedupRelationship.isEmpty()) {
      // Relationship may belong to a many-to-many relationship, so compute the
      // cardinality
      return EntityModelUtility.inferCardinality(tableReference);
    }
    final RelationshipCardinality cardinality = lookedupRelationship.get().getType();
    return cardinality;
  }
}

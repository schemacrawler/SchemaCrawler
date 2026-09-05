/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.model.builder;

import static schemacrawler.schema.TableConstraintType.implicit_association;

import java.util.Collection;
import java.util.logging.Logger;
import org.jgrapht.Graph;
import schemacrawler.importance.model.DatabaseObjectNodeId;
import schemacrawler.importance.model.EdgeType;
import schemacrawler.importance.model.SchemaEdge;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ReferencingObject;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableReference;
import schemacrawler.schema.View;
import us.fatehi.utility.UtilityMarker;

/** Adds typed dependency edges from catalog metadata to a schema graph. */
@UtilityMarker
public final class EdgeFactory {

  private static final Logger LOGGER = Logger.getLogger(EdgeFactory.class.getName());

  public static void addEdges(
      final Collection<? extends Table> tables,
      final Collection<? extends Routine> routines,
      final Collection<? extends Synonym> synonyms,
      final Graph<DatabaseObjectNodeId, SchemaEdge> graph) {
    for (final Table table : tables) {
      addForeignKeyEdges(table, graph);
      addImpliedAssociationEdges(table, graph);
      if (table instanceof final View view) {
        addReferencedObjectEdges(view, EdgeType.VIEW_DEPENDENCY, graph);
      }
    }
    for (final Routine routine : routines) {
      addReferencedObjectEdges(routine, EdgeType.ROUTINE_DEPENDENCY, graph);
    }
    for (final Synonym synonym : synonyms) {
      if (synonym.hasReferencedObject()) {
        addEdge(synonym, synonym.getReferencedObject(), EdgeType.SYNONYM_RESOLUTION, null, graph);
      }
    }
  }

  private static void addEdge(
      final DatabaseObject source,
      final DatabaseObject target,
      final EdgeType edgeType,
      final schemacrawler.schema.NamedObjectKey referenceKey,
      final Graph<DatabaseObjectNodeId, SchemaEdge> graph) {
    if (source == null || target == null) {
      LOGGER.warning(() -> "Skipping " + edgeType + " edge with a missing endpoint");
      return;
    }
    final DatabaseObjectNodeId sourceNode = NodeIdFactory.create(source);
    final DatabaseObjectNodeId targetNode = NodeIdFactory.create(target);
    if (!graph.containsVertex(sourceNode) || !graph.containsVertex(targetNode)) {
      LOGGER.warning(
          () ->
              "Skipping "
                  + edgeType
                  + " edge because a referenced object is not part of the catalog");
      return;
    }
    final SchemaEdge edge = new SchemaEdge(edgeType, referenceKey);
    graph.addEdge(sourceNode, targetNode, edge);
  }

  private static void addForeignKeyEdges(
      final Table table, final Graph<DatabaseObjectNodeId, SchemaEdge> graph) {
    for (final ForeignKey foreignKey : table.getImportedForeignKeys()) {
      addEdge(
          table, foreignKey.getPrimaryKeyTable(), EdgeType.FOREIGN_KEY, foreignKey.key(), graph);
    }
  }

  private static void addImpliedAssociationEdges(
      final Table table, final Graph<DatabaseObjectNodeId, SchemaEdge> graph) {
    for (final TableConstraint constraint : table.getTableConstraints()) {
      if (constraint.getType() == implicit_association
          && constraint instanceof final TableReference reference) {
        addEdge(
            table,
            reference.getPrimaryKeyTable(),
            EdgeType.IMPLICIT_ASSOCIATION,
            reference.key(),
            graph);
      }
    }
  }

  private static void addReferencedObjectEdges(
      final ReferencingObject source,
      final EdgeType edgeType,
      final Graph<DatabaseObjectNodeId, SchemaEdge> graph) {
    final DatabaseObject sourceObject = (DatabaseObject) source;
    for (final DatabaseObject referencedObject : source.getReferencedObjects()) {
      addEdge(sourceObject, referencedObject, edgeType, null, graph);
    }
  }

  private EdgeFactory() {
    // Prevent instantiation
  }
}

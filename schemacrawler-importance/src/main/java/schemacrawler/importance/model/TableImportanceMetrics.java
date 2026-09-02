/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.model;

/** Immutable topology metrics for one schema graph object. */
public record TableImportanceMetrics(
    int inDegree,
    int outDegree,
    double betweennessCentrality,
    int dependencyReachabilityCount,
    int impactReachabilityCount) {}

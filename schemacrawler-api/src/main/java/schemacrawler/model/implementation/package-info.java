/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

/**
 * Internal implementation package for mutable model classes.
 *
 * <p>This package contains the mutable implementation classes for the SchemaCrawler schema model.
 * These classes are used internally by the crawling and retrieval logic in the {@link
 * schemacrawler.crawl} package to build and populate the database schema model.
 *
 * <p><strong>Important:</strong> Classes in this package are internal implementation details and
 * should not be used directly by external code. The public API exposes only the immutable interface
 * types from the {@link schemacrawler.schema} package.
 *
 * <p>This package is not exported from the module, ensuring that these implementation details
 * remain encapsulated when using JPMS (Java Platform Module System). The moditect-maven-plugin
 * configuration in the schemacrawler module explicitly excludes this package from the module
 * exports, providing proper encapsulation on the module path.
 *
 * <p>Package contents include:
 *
 * <ul>
 *   <li>Mutable* classes - Implementation classes for schema objects (tables, columns, indexes,
 *       etc.)
 *   <li>Abstract* classes - Base classes providing common functionality for mutable implementations
 *   <li>Helper classes - Supporting classes like NamedObjectList, DatabaseObjectReference, and
 *       various Pointer and Partial types
 * </ul>
 *
 * @since 17.1.7
 */
package schemacrawler.model.implementation;

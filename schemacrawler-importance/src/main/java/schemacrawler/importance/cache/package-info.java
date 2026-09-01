/**
 * Immutable schema dependency graph and cached importance metrics.
 *
 * <p>Edges point from a dependent object to the object it references. Consequently, an object's
 * out-degree and forward reachability represent prerequisites, while its in-degree and reverse
 * reachability represent direct and transitive change impact. Metrics are stored for every graph
 * node; {@link schemacrawler.importance.cache.TableImportance} is additionally attached only to
 * tables and views under its fully qualified class-name attribute key.
 */
package schemacrawler.importance.cache;

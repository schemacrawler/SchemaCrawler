/**
 * Factories and builders for schema dependency graphs.
 *
 * <p>Typed node identifiers distinguish objects that share a named-object key. The graph builder
 * preserves declared foreign keys, view and routine dependencies, and synonym resolution; it also
 * keeps implicit associations available for navigation while excluding them from topology metrics.
 */
package schemacrawler.importance.util;

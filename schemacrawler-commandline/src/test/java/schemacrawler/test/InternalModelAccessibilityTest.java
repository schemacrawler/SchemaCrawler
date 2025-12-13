/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

/**
 * Test to verify that internal model implementation classes from the
 * schemacrawler.model.implementation package are not accessible from external modules.
 *
 * <p>This test ensures that the mutable model implementation classes remain internal
 * implementation details and are not part of the public API.
 *
 * <p>The schemacrawler.model.implementation package is not exported from the module descriptor
 * (configured via moditect-maven-plugin). This test verifies encapsulation by checking that
 * internal classes cannot be loaded via Class.forName().
 *
 * <p><strong>Note:</strong> Class.forName() behavior differs between classpath and module path:
 * <ul>
 *   <li>On the <strong>classpath</strong>: Classes remain accessible regardless of module exports,
 *       but this test may still pass if dependencies are properly isolated</li>
 *   <li>On the <strong>module path</strong>: Non-exported packages are strictly inaccessible,
 *       and Class.forName() will throw ClassNotFoundException</li>
 * </ul>
 *
 * <p>This test validates proper dependency isolation and documents the expected JPMS behavior.
 */
public class InternalModelAccessibilityTest {

  /**
   * Test that internal model implementation classes cannot be directly imported or accessed.
   *
   * <p>This test uses reflection to verify that the internal implementation package is not
   * accessible from this module. The schemacrawler.model.implementation package is not exported
   * in the module descriptor, so these classes should not be accessible via Class.forName().
   *
   * <p>When running on the module path with JPMS enabled, all internal classes should throw
   * ClassNotFoundException. When running on the classpath, the test validates proper dependency
   * isolation - the schemacrawler-commandline module should not have direct access to
   * schemacrawler-api internals.
   */
  @Test
  public void internalModelShouldNotBeAccessible() {
    // List of internal implementation classes that should not be accessible
    final String[] internalClasses = {
      "schemacrawler.model.implementation.MutableColumn",
      "schemacrawler.model.implementation.MutableTable",
      "schemacrawler.model.implementation.MutableIndex",
      "schemacrawler.model.implementation.MutableIndexColumn",
      "schemacrawler.model.implementation.MutableKeyColumn",
      "schemacrawler.model.implementation.MutableColumnDataType",
      "schemacrawler.model.implementation.MutableResultsColumn",
      "schemacrawler.model.implementation.MutableResultsColumns",
      "schemacrawler.model.implementation.MutableTableConstraintColumn",
      "schemacrawler.model.implementation.AbstractColumn",
      "schemacrawler.model.implementation.AbstractDatabaseObject",
      "schemacrawler.model.implementation.NamedObjectList"
    };

    int inaccessibleCount = 0;
    int accessibleCount = 0;
    
    for (final String className : internalClasses) {
      try {
        // Attempt to load the class
        final Class<?> clazz = Class.forName(className);
        
        // If we reach here, the class is accessible
        // This should NOT happen when running on the module path with JPMS
        // On classpath, this indicates the dependency isolation is not complete
        accessibleCount++;
        
      } catch (final ClassNotFoundException e) {
        // This is the EXPECTED and DESIRED behavior!
        // The internal implementation package is not accessible from this module,
        // which confirms that the internal model classes are properly encapsulated.
        inaccessibleCount++;
      }
    }
    
    // All internal classes should be inaccessible for proper encapsulation
    // On the module path with JPMS: all classes must be inaccessible (strict enforcement)
    // On the classpath: all classes should be inaccessible (proper dependency isolation)
    assertThat(
        "All internal model classes must be inaccessible from external modules. " +
        "Found " + inaccessibleCount + " inaccessible and " + accessibleCount + " accessible " +
        "out of " + internalClasses.length + " total. " +
        "When JPMS is active, all internal classes should throw ClassNotFoundException.",
        inaccessibleCount,
        is(internalClasses.length));
  }

  /**
   * Test that only public API interfaces from schemacrawler.schema package are accessible.
   *
   * <p>This verifies that the public API interfaces remain accessible while the internal
   * implementation classes are hidden.
   */
  @Test
  public void publicApiShouldBeAccessible() {
    // List of public API interfaces that should always be accessible
    final String[] publicInterfaces = {
      "schemacrawler.schema.Column",
      "schemacrawler.schema.Table",
      "schemacrawler.schema.Index",
      "schemacrawler.schema.IndexColumn",
      "schemacrawler.schema.ColumnDataType",
      "schemacrawler.schema.ResultsColumn",
      "schemacrawler.schema.Catalog"
    };

    for (final String interfaceName : publicInterfaces) {
      try {
        final Class<?> interfaceClass = Class.forName(interfaceName);
        assertThat("Public interface should be accessible: " + interfaceName, 
                   interfaceClass, is(notNullValue()));
        assertThat("Should be an interface", interfaceClass.isInterface(), is(true));
      } catch (final ClassNotFoundException e) {
        fail("Public API interface should be accessible: " + interfaceName);
      }
    }
  }
}

/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static com.tngtech.archunit.core.importer.ImportOption.Predefined.DO_NOT_INCLUDE_TESTS;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.schema.NamedObject;

/**
 * ArchUnit tests to ensure package architecture and prevent package cycles in schemacrawler-api.
 * These tests guard against regressions of the refactoring that moved Identifiers-related classes
 * from schemacrawler.schemacrawler to schemacrawler.schema to break a package cycle.
 */
@TestInstance(PER_CLASS)
public class PackageCycleTest {

  private JavaClasses classes;

  @BeforeAll
  public void loadClasses() {
    final String description = "SchemaCrawler API";
    classes =
        new ClassFileImporter()
            .withImportOption(DO_NOT_INCLUDE_TESTS)
            .importPackages("schemacrawler..")
            .as(description);
    assertThat(description + " classes not found", classes.isEmpty(), is(false));
  }

  /**
   * Test that Identifiers classes are NOT in the schemacrawler.schemacrawler package. This prevents
   * a package cycle where schema depended on schemacrawler.Identifiers and schemacrawler depended
   * on schema entities.
   */
  @Test
  public void identifiersClassesAreNotInSchemaCrawlerPackage() {
    noClasses()
        .that()
        .haveSimpleNameStartingWith("Identifier")
        .should()
        .resideInAPackage("schemacrawler.schemacrawler..")
        .because(
            "Identifiers classes should be in schema package to avoid package cycle - they were"
                + " moved there in the refactoring")
        .check(classes);
  }

  /**
   * Test that schema domain entities (those implementing NamedObject, Schema, DatabaseObject) do
   * not depend on concrete classes in schemacrawler package. Dependencies on framework interfaces
   * like Options and OptionsBuilder are acceptable, but not on concrete options implementations.
   * This prevents the tight coupling that existed before the refactoring.
   */
  @Test
  public void schemaDomainEntitiesDoNotDependOnSchemaCrawlerConcreteClasses() {
    noClasses()
        .that()
        .resideInAPackage("schemacrawler.schema..")
        .and()
        .implement(JavaClass.Predicates.assignableTo(NamedObject.class))
        .should()
        .dependOnClassesThat()
        .resideInAPackage("schemacrawler.schemacrawler..")
        .andShould()
        .notBeInterfaces()
        .andShould()
        .haveSimpleNameNotContaining("Exception")
        .because(
            "schema domain entities should not depend on concrete schemacrawler classes to avoid"
                + " tight coupling")
        .allowEmptyShould(true)
        .check(classes);
  }
}

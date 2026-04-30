/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.simpleName;
import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.core.importer.ImportOption.Predefined.DO_NOT_INCLUDE_TESTS;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.are;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.THROW_GENERIC_EXCEPTIONS;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.schemacrawler.ModelImplementation;
import schemacrawler.schemacrawler.Retriever;

@TestInstance(PER_CLASS)
public class ArchitectureTest {

  private JavaClasses classes;

  @BeforeAll
  public void _classes() {
    final String description = "SchemaCrawler production classes";
    classes =
        new ClassFileImporter()
            .withImportOption(DO_NOT_INCLUDE_TESTS)
            .withImportOption(location -> !location.matches(Pattern.compile(".*[Tt]est.*")))
            .importPackages("schemacrawler..")
            .as(description);
    assertThat(description + " classes not found", classes.isEmpty(), is(false));
  }

  @Test
  public void lookupMethods() {
    final Optional<JavaMethod> anyMatchingMethod =
        classes.stream()
            .flatMap(c -> c.getMethods().stream())
            .filter(m -> m.getName().matches("lookup.*"))
            .filter(m -> m.getModifiers().contains(JavaModifier.PUBLIC))
            .findAny();
    assertThat(anyMatchingMethod.isPresent(), is(true));

    methods()
        .that()
        .haveNameMatching("lookup.*")
        .and()
        .arePublic()
        .should()
        .haveRawReturnType(Optional.class)
        .because("lookups may not return a value")
        .check(classes);
  }

  // The schemacrawler.crawl package is intentionally kept flat (not split into subpackages).
  //
  // All Mutable* model implementations and *Retriever JDBC extractors are package-private.
  // This prevents both module-path and classpath users from constructing or referencing
  // these internal classes.
  //
  // Java package-private visibility is strictly per-package. Splitting into subpackages would
  // require making these classes at least public — immediately exposing them.
  // Although schemacrawler.crawl is exported from the JPMS module (for MetadataResultSet and
  // ResultsCrawler access), the package-private Mutable* and @Retriever classes remain
  // inaccessible. These tests enforce the architectural boundary.
  @Test
  public void model() {

    noClasses()
        .that()
        .resideOutsideOfPackage("schemacrawler.crawl")
        .should()
        .dependOnClassesThat(
            resideInAPackage("schemacrawler.crawl").and(annotatedWith(ModelImplementation.class)))
        .because(
            """
            @ModelImplementation classes in schemacrawler.crawl are package-private internal
              schema model implementations; they must only be used within schemacrawler.crawl
              to prevent classpath clients from constructing schema model objects directly
            """)
        .check(classes);

    noClasses()
        .that()
        .resideOutsideOfPackage("schemacrawler.ermodel.implementation")
        .should()
        .dependOnClassesThat(
            resideInAPackage("schemacrawler.ermodel.implementation")
                .and(annotatedWith(ModelImplementation.class)))
        .because(
            """
            @ModelImplementation classes in schemacrawler.ermodel.implementation are internal
              ER model implementations; they must only be used within that package
            """)
        .check(classes);

    noClasses()
        .that()
        .resideOutsideOfPackages(
            "schemacrawler.loader.catalog.model", "schemacrawler.loader.ermodel.attributes")
        .should()
        .dependOnClassesThat(
            resideInAPackage("schemacrawler.loader.catalog.model")
                .and(annotatedWith(ModelImplementation.class)))
        .because(
            """
            @ModelImplementation classes in schemacrawler.loader.catalog.model are internal YAML
              deserialization DTOs; only AttributesLoader in ermodel.attributes (the designated
              processor) is permitted to reference them outside the model package
            """)
        .check(classes);

    noClasses()
        .that()
        .resideOutsideOfPackage("schemacrawler.crawl")
        .should()
        .dependOnClassesThat(annotatedWith(Retriever.class))
        .because(
            """
            @Retriever classes in schemacrawler.crawl are package-private JDBC metadata
              extractors; they must only be used within schemacrawler.crawl
            """)
        .check(classes);

    classes()
        .that()
        .areAnnotatedWith(ModelImplementation.class)
        .should()
        .notBePublic()
        .because(
            """
            @ModelImplementation classes are package-private internal implementations;
              declaring them public exposes them to classpath clients
            """)
        .check(classes);

    classes()
        .that()
        .areAnnotatedWith(Retriever.class)
        .should()
        .notBePublic()
        .because(
            """
            @Retriever classes are package-private JDBC metadata extractors;
              declaring them public exposes them to classpath clients
            """)
        .check(classes);
  }

  @Test
  public void notAccessStandardStreams() {
    noClasses()
        .should(ACCESS_STANDARD_STREAMS)
        .because("production code should not write to standard streams")
        .check(classes);
  }

  @Test
  public void notThrowGenericExceptions() {
    noClasses()
        .should(THROW_GENERIC_EXCEPTIONS)
        .because(
            "SchemaCrawler defines it own exceptions, and wraps SQL exceptions with additional"
                + " information")
        .check(classes);
  }

  @Test
  public void packageCycles() {
    slices()
        .matching("schemacrawler.(**)..")
        .as("SchemaCrawler production classes")
        .should()
        .beFreeOfCycles()
        .because("packages should have a clear, acyclic dependency structure")
        .check(classes);
  }

  @Test
  public void reflectiveAccessOverride() {
    noClasses()
        .should()
        .callMethod(AccessibleObject.class, "setAccessible", boolean.class)
        .orShould()
        .callMethod(
            AccessibleObject.class, "setAccessible", AccessibleObject[].class, boolean.class)
        .because("avoid reflective access override")
        .check(classes);
  }

  @Test
  public void reflectiveClassLoading() {
    noClasses()
        .that(are(not(simpleName("BasePluginCommandRegistry"))))
        .should()
        .callMethod(Class.class, "forName", String.class)
        .orShould()
        .callMethod(Class.class, "getDeclaredConstructors")
        .orShould()
        .callMethod(Class.class, "getDeclaredConstructor", Class[].class)
        .orShould()
        .callMethod(Class.class, "getConstructor", Class[].class)
        .orShould()
        .callMethod(Constructor.class, "newInstance")
        .because("avoid reflective class loading")
        .check(classes);
  }
}

package schemacrawler.test;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideOutsideOfPackages;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.simpleName;
import static com.tngtech.archunit.core.importer.ImportOption.Predefined.DO_NOT_INCLUDE_ARCHIVES;
import static com.tngtech.archunit.core.importer.ImportOption.Predefined.DO_NOT_INCLUDE_TESTS;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.are;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;
import static com.tngtech.archunit.library.GeneralCodingRules.ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.THROW_GENERIC_EXCEPTIONS;
import static com.tngtech.archunit.library.GeneralCodingRules.USE_JAVA_UTIL_LOGGING;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

public class ArchitectureTest {

  private final JavaClasses classes =
      new ClassFileImporter()
          .withImportOption(DO_NOT_INCLUDE_ARCHIVES)
          .withImportOption(DO_NOT_INCLUDE_TESTS)
          .importPackages("schemacrawler..");

  @Test
  public void accessStandardStreams() {
    noClasses()
        .that(resideOutsideOfPackages("schemacrawler.testdb").and(are(not(simpleName("Version")))))
        .should(ACCESS_STANDARD_STREAMS)
        .because("production code should not write to standard streams")
        .check(classes);
  }

  @Disabled
  @Test
  public void architecture() {
    onionArchitecture()
        .domainModels("schemacrawler.schema..")
        .domainServices("schemacrawler.crawl..")
        .applicationServices("schemacrawler.analysis.(*)..")
        /*
        .adapter("cli", "com.myapp.adapter.cli..")
        .adapter("persistence", "com.myapp.adapter.persistence..")
        .adapter("rest", "com.myapp.adapter.rest..")
         */

        .check(classes);
  }

  @Test
  public void architectureCycles() {
    slices()
        .matching("schemacrawler.(*)..")
        .should()
        .beFreeOfCycles()
        .because("code should be well-structured in packages")
        .check(classes);
  }

  @Test
  public void lookupMethods() {
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

  @Test
  public void notThrowGenericExceptions() {
    noClasses()
        .that(resideOutsideOfPackages("schemacrawler.testdb", "sf.util"))
        .should(THROW_GENERIC_EXCEPTIONS)
        .because(
            "SchemaCrawler defines it own exceptions, and wraps SQL exceptions with additional information")
        .check(classes);
  }

  @Test
  public void notUseJavaLogging() {
    noClasses()
        .that(
            resideOutsideOfPackages("schemacrawler.testdb", "sf.util")
                .and(are(not(simpleName("SchemaCrawlerLogger"))))
                .and(are(not(simpleName("LogLevel")))))
        .should(USE_JAVA_UTIL_LOGGING)
        .because("SchemaCrawler wraps Java logging in a utility")
        .check(classes);
  }
}

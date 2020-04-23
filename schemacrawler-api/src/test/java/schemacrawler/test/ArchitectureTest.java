package schemacrawler.test;


import static com.tngtech.archunit.base.DescribedPredicate.doNot;
import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.simpleName;
import static com.tngtech.archunit.core.importer.ImportOption.Predefined.DO_NOT_INCLUDE_ARCHIVES;
import static com.tngtech.archunit.core.importer.ImportOption.Predefined.DO_NOT_INCLUDE_TESTS;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.are;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;
import static com.tngtech.archunit.library.GeneralCodingRules.ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.THROW_GENERIC_EXCEPTIONS;
import static com.tngtech.archunit.library.GeneralCodingRules.USE_JAVA_UTIL_LOGGING;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ArchitectureTest
{

  private final JavaClasses classes = new ClassFileImporter()
    .withImportOption(DO_NOT_INCLUDE_ARCHIVES)
    .withImportOption(DO_NOT_INCLUDE_TESTS)
    .importPackages("schemacrawler..");

  @Test
  public void architectureCycles()
  {
    slices()
      .matching("schemacrawler.(*)..")
      .should()
      .beFreeOfCycles()

      .check(classes);
  }

  @Test
  public void accessStandardStreams()
  {
    noClasses()
      .that(doNot(resideInAPackage("schemacrawler.testdb")).and(are(not(simpleName("Version")))))
      .should(ACCESS_STANDARD_STREAMS)
      .check(classes);
  }

  @Test
  public void notThrowGenericExceptions()
  {
    noClasses()
      .that(doNot(resideInAPackage("schemacrawler.testdb")))
      .should(THROW_GENERIC_EXCEPTIONS)
      .check(classes);
  }

  @Test
  public void notUseJavaLogging()
  {
    noClasses()
      .that(doNot(resideInAPackage("schemacrawler.testdb")))
      .should(USE_JAVA_UTIL_LOGGING)
      .check(classes);
  }

  @Disabled
  @Test
  public void architecture()
  {
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

}

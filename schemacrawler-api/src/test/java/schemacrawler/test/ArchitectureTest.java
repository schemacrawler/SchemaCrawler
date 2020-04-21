package schemacrawler.test;


import static com.tngtech.archunit.core.importer.ImportOption.Predefined.DO_NOT_INCLUDE_JARS;
import static com.tngtech.archunit.core.importer.ImportOption.Predefined.DO_NOT_INCLUDE_TESTS;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ArchitectureTest
{

  private final JavaClasses classes = new ClassFileImporter()
    .withImportOption(DO_NOT_INCLUDE_JARS)
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

  @Disabled
  @Test
  public void architectureLayers()
  {
    layeredArchitecture()
      .layer("Crawler")
      .definedBy("schemacrawler.crawl..")
      .layer("Filter")
      .definedBy("schemacrawler.filter..")

      .whereLayer("Crawler")
      .mayNotBeAccessedByAnyLayer()
      .whereLayer("Filter")
      .mayOnlyBeAccessedByLayers("Crawler")

      .check(classes);
  }

}

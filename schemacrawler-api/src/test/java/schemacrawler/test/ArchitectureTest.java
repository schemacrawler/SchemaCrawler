package schemacrawler.test;


import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

public class ArchitectureTest
{

  private final JavaClasses classes = new ClassFileImporter().importPackages("schemacrawler..");

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

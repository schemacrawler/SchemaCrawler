/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.simpleName;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.are;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.Test;

public class ArchitectureTest extends BaseArchitectureTest {

  @Override
  @Test
  public void notUseJackson() {
    noClasses()
        .that(are(not(simpleName("CatalogAttributesUtility"))))
        .should()
        .dependOnClassesThat()
        .resideInAPackage("tools.jackson..")
        .because("SchemaCrawler-Core must not depend on Jackson")
        .check(classes);
  }

  @Override
  protected String classesSpecification() {
    return "schemacrawler..";
  }
}

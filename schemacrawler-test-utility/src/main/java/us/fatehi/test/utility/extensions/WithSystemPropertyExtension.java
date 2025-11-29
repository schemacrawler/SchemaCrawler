/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test.utility.extensions;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Optional;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

final class WithSystemPropertyExtension implements BeforeEachCallback, AfterEachCallback {

  private SimpleImmutableEntry<String, String> systemProperty;

  @Override
  public void afterEach(final ExtensionContext context) throws Exception {
    if (systemProperty == null) {
      throw new IllegalArgumentException("No system property to clear");
    }
    System.clearProperty(systemProperty.getKey());
    systemProperty = null;
  }

  @Override
  public void beforeEach(final ExtensionContext context) throws Exception {
    final Optional<WithSystemProperty> optionalAnnotation =
        findAnnotation(context.getTestMethod(), WithSystemProperty.class);
    optionalAnnotation.ifPresent(
        withSystemProperty -> {
          final String key = withSystemProperty.key();
          if (isBlank(key)) {
            throw new IllegalArgumentException("No system property name provided");
          }
          systemProperty = new SimpleImmutableEntry<>(key, withSystemProperty.value());
          System.setProperty(systemProperty.getKey(), systemProperty.getValue());
        });
  }
}

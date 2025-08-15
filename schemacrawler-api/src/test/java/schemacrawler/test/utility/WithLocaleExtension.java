/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;
import static us.fatehi.utility.Utility.isBlank;

import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

final class WithLocaleExtension implements BeforeEachCallback, AfterEachCallback {

  private Locale systemLocale;

  @Override
  public void afterEach(final ExtensionContext context) throws Exception {
    if (systemLocale != null) {
      Locale.setDefault(systemLocale);
    } else {
      Locale.setDefault(Locale.US);
    }
    systemLocale = null;
  }

  @Override
  public void beforeEach(final ExtensionContext context) throws Exception {
    final Optional<WithLocale> optionalAnnotation =
        findAnnotation(context.getTestMethod(), WithLocale.class);
    optionalAnnotation.ifPresent(
        withLocale -> {
          final String localeString = withLocale.value();
          if (isBlank(localeString)) {
            throw new IllegalArgumentException("No locale provided");
          }
          systemLocale = Locale.getDefault();
          final Locale locale = Locale.forLanguageTag(localeString);
          Locale.setDefault(locale);
        });
  }
}

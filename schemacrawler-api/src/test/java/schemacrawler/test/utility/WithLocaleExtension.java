/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
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

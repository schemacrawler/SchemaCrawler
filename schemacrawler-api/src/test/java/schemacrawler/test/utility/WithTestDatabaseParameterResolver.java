/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility;

import java.lang.reflect.Parameter;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import us.fatehi.test.utility.extensions.UsingTestDatabaseParameterResolver;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

final class WithTestDatabaseParameterResolver extends UsingTestDatabaseParameterResolver {

  private static boolean isParameterDatabaseConnectionSource(final Parameter parameter) {
    return parameter.getType().isAssignableFrom(DatabaseConnectionSource.class);
  }

  public WithTestDatabaseParameterResolver() {
    super(WithTestDatabase.class);
  }

  @Override
  public Object resolveParameter(
      final ParameterContext parameterContext, final ExtensionContext extensionContext)
      throws ParameterResolutionException {
    final Parameter parameter = parameterContext.getParameter();
    if (isParameterDatabaseConnectionSource(parameter)) {
      return DatabaseConnectionSources.fromDataSource(getDataSource());
    }
    return super.resolveParameter(parameterContext, extensionContext);
  }

  @Override
  public boolean supportsParameter(
      final ParameterContext parameterContext, final ExtensionContext extensionContext)
      throws ParameterResolutionException {
    final Parameter parameter = parameterContext.getParameter();
    final boolean hasDatabaseConnectionSource = isParameterDatabaseConnectionSource(parameter);
    return super.supportsParameter(parameterContext, extensionContext)
        || hasDatabaseConnectionSource;
  }
}

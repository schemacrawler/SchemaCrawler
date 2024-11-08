/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.crawl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoClassFilter;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.reflection.java.Java;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.EqualsAndHashCodeMatchRule;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.NoPublicFieldsExceptStaticFinalRule;
import com.openpojo.validation.rule.impl.NoStaticExceptFinalRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

public class GettersSettersTest {

  private static class FilterPackageClasses implements PojoClassFilter {

    private final List<String> excludeClasses;

    FilterPackageClasses(final String... excludeClasses) {
      this.excludeClasses = Arrays.asList(excludeClasses);
    }

    @Override
    public boolean include(final PojoClass pojoClass) {
      final Class<?> pojoClassClazz = pojoClass.getClazz();
      return !pojoClass.getName().endsWith(Java.PACKAGE_DELIMITER + Java.PACKAGE_INFO)
          && !pojoClassClazz.isEnum()
          && !excludeClasses.contains(pojoClassClazz.getSimpleName());
    }
  }

  // Configured for expectation, so we know when a class gets added or removed.
  private static final int EXPECTED_CLASS_COUNT = 53;

  private static final String PACKAGE_SCHEMACRAWLER_SCHEMA = "schemacrawler.schema";

  @Test
  public void accessors() {
    final List<PojoClass> pojoClasses =
        PojoClassFactory.getPojoClasses(PACKAGE_SCHEMACRAWLER_SCHEMA, new FilterPackageClasses());
    assertThat("Classes added or removed?", pojoClasses.size(), is(EXPECTED_CLASS_COUNT));

    final Validator validator =
        ValidatorBuilder.create()
            .with(new EqualsAndHashCodeMatchRule())
            .with(new NoStaticExceptFinalRule())
            .with(new NoPublicFieldsExceptStaticFinalRule())
            .with(new GetterMustExistRule())
            .with(new SetterMustExistRule())
            .with(new SetterTester())
            .with(new GetterTester())
            .build();

    validator.validate(
        PACKAGE_SCHEMACRAWLER_SCHEMA,
        new FilterPackageClasses("NamedObjectKey", "JavaSqlType", "TableTypes"));
  }
}

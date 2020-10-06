/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.executable.commandline;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class PluginCommandPojoTest {

  @Test
  public void pluginCommandOption() {
    EqualsVerifier.forClass(PluginCommandOption.class)
        .suppress(Warning.STRICT_INHERITANCE)
        .withOnlyTheseFields("name")
        .verify();

    final Validator validator =
        ValidatorBuilder.create().with(new GetterMustExistRule()).with(new GetterTester()).build();
    validator.validate(PojoClassFactory.getPojoClass(PluginCommandOption.class));

    final PluginCommandOption pluginCommandOption =
        new PluginCommandOption("name", "helpText", this.getClass());
    assertThat(
        pluginCommandOption.toString(),
        is(
            "PluginCommandOption[name='name', valueClass=schemacrawler.tools.executable.commandline.PluginCommandPojoTest]"));
  }

  @Test
  public void pluginCommand() {
    EqualsVerifier.forClass(PluginCommand.class)
        .suppress(Warning.STRICT_INHERITANCE)
        .withOnlyTheseFields("name")
        .verify();

    final PluginCommand pluginCommand = PluginCommand.newDatabasePluginCommand("name", "helpText");
    assertThat(pluginCommand.toString(), is("PluginCommand[name='name', options=[]]"));
  }
}

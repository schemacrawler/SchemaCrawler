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

package schemacrawler.tools.executable.commandline;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import org.junit.jupiter.api.Test;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import us.fatehi.utility.property.PropertyName;

public class PluginCommandPojoTest {

  @Test
  public void emptyCommand() {

    final PluginCommand pluginCommand = PluginCommand.empty();
    assertThat(pluginCommand.toString(), is("PluginCommand[name='unknown', options=[]]"));
    assertThat(pluginCommand.getName(), is("unknown:unknown"));
    assertThat(pluginCommand.getHelpHeader(), is(""));
    assertThat(pluginCommand.getHelpDescription(), is(not(nullValue())));
    assertThat(pluginCommand.getHelpFooter().get(), is(arrayWithSize(0)));
    assertThat(pluginCommand.getOptions(), is(empty()));
    assertThat(pluginCommand.hasHelpDescription(), is(false));
    assertThat(pluginCommand.hasHelpFooter(), is(false));
    assertThat(pluginCommand.isEmpty(), is(true));
  }

  @Test
  public void newCatalogLoaderCommand() {
    final PluginCommand pluginCommand = PluginCommand.newCatalogLoaderCommand("name", "helpHeader");
    pluginCommand.addOption("option1", Object.class, "helpOption1", "helpOption2");
    assertPluginCommandValues(
        pluginCommand, "Add loader options to the `load` command in the SchemaCrawler Shell");
  }

  @Test
  public void newDatabasePluginCommand() {
    final PluginCommand pluginCommand =
        PluginCommand.newDatabasePluginCommand("name", "helpHeader");
    pluginCommand.addOption("option1", Object.class, "helpOption1", "helpOption2");
    assertPluginCommandValues(
        pluginCommand,
        "Add connection options to the `connect` command in the SchemaCrawler Shell");
  }

  @Test
  public void newPluginCommand() {
    final PluginCommand pluginCommand =
        PluginCommand.newPluginCommand(new PropertyName("name", "** helpHeader"));
    pluginCommand.addOption("option1", Object.class, "helpOption1", "helpOption2");
    assertPluginCommandValues(
        pluginCommand, "Add command options to the `execute` command in the SchemaCrawler Shell");
  }

  @Test
  public void pluginCommand() {
    EqualsVerifier.forClass(PluginCommand.class)
        .suppress(Warning.STRICT_INHERITANCE)
        .withOnlyTheseFields("name")
        .verify();

    final PluginCommand pluginCommand =
        PluginCommand.newDatabasePluginCommand("name", "helpHeader");
    assertThat(pluginCommand.toString(), is("PluginCommand[name='name', options=[]]"));
  }

  @Test
  public void pluginCommandExtraMethods() {

    final PluginCommand pluginCommand =
        PluginCommand.newPluginCommand(
            new PropertyName("name", "helpHeader"),
            () -> new String[] {"helpDescription1", "helpDescription2"},
            () -> new String[] {"helpFooter1", "helpFooter2"});
    assertThat(pluginCommand.toString(), is("PluginCommand[name='name', options=[]]"));
    assertThat(pluginCommand.getName(), endsWith(":name"));
    assertThat(pluginCommand.getHelpHeader(), is("** helpHeader"));
    assertThat(
        pluginCommand.getHelpDescription().get(),
        is(arrayContaining("helpDescription1", "helpDescription2")));
    assertThat(
        pluginCommand.getHelpFooter().get(),
        is(
            arrayContaining(
                "helpFooter1",
                "helpFooter2",
                "Add command options to the `execute` command in the SchemaCrawler Shell")));
    assertThat(pluginCommand.getOptions(), is(empty()));
    assertThat(pluginCommand.hasHelpDescription(), is(true));
    assertThat(pluginCommand.hasHelpFooter(), is(true));
    assertThat(pluginCommand.isEmpty(), is(false));
  }

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
        new PluginCommandOption("name", this.getClass(), "helpText");
    assertThat(
        pluginCommandOption.toString(),
        is(
            "PluginCommandOption[name='name', valueClass=schemacrawler.tools.executable.commandline.PluginCommandPojoTest]"));
  }

  @Test
  public void pluginCommandOptionEmpty() {
    final PluginCommandOption pluginCommandOption =
        new PluginCommandOption("option1", (Class<?>) null, (String[]) null);
    assertThat(pluginCommandOption.getValueClass(), is(String.class));
    assertThat(pluginCommandOption.getHelpText(), is(arrayWithSize(0)));
  }

  private void assertPluginCommandValues(
      final PluginCommand pluginCommand, final String standardFooter) {

    final PluginCommandOption option =
        new PluginCommandOption("option1", Object.class, "helpOption1", "helpOption2");

    assertThat(
        pluginCommand.toString(),
        is(
            "PluginCommand[name='name', options=[PluginCommandOption[name='option1', valueClass=java.lang.Object]]]"));
    assertThat(pluginCommand.getName(), endsWith(":name"));
    assertThat(pluginCommand.getHelpHeader(), is("** helpHeader"));
    assertThat(pluginCommand.getHelpDescription(), is(not(nullValue())));
    assertThat(pluginCommand.getHelpFooter().get(), is(arrayContaining(standardFooter)));
    assertThat(pluginCommand.getOptions(), contains(option));
    assertThat(pluginCommand.hasHelpDescription(), is(false));
    assertThat(pluginCommand.hasHelpFooter(), is(true));
    assertThat(pluginCommand.isEmpty(), is(false));
    for (final PluginCommandOption pluginCommandOption : pluginCommand) {
      assertThat(pluginCommandOption, is(option));
    }
  }
}

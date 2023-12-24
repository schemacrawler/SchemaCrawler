/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import schemacrawler.loader.attributes.AttributesCatalogLoader;
import schemacrawler.loader.counts.TableRowCountsCatalogLoader;
import schemacrawler.loader.weakassociations.WeakAssociationsCatalogLoader;
import schemacrawler.tools.executable.commandline.PluginCommand;

public class LoaderPluginCommandTest {

  @Test
  public void loaderPluginCommands() {

    final PluginCommand rowCountsPluginCommand =
        new TableRowCountsCatalogLoader().getCommandLineCommand();
    assertThat(
        rowCountsPluginCommand.toString(),
        is(
            "PluginCommand[name='countsloader', options=["
                + "PluginCommandOption[name='load-row-counts', valueClass=java.lang.Boolean], "
                + "PluginCommandOption[name='no-empty-tables', valueClass=java.lang.Boolean]"
                + "]]"));

    final PluginCommand attributesPluginCommand =
        new AttributesCatalogLoader().getCommandLineCommand();
    assertThat(
        attributesPluginCommand.toString(),
        is(
            "PluginCommand[name='attributesloader', options=["
                + "PluginCommandOption[name='attributes-file', valueClass=java.lang.String]"
                + "]]"));

    final PluginCommand weakAssociationsPluginCommand =
        new WeakAssociationsCatalogLoader().getCommandLineCommand();
    assertThat(
        weakAssociationsPluginCommand.toString(),
        is(
            "PluginCommand[name='weakassociationsloader', options=["
                + "PluginCommandOption[name='weak-associations', valueClass=java.lang.Boolean], "
                + "PluginCommandOption[name='infer-extension-tables', valueClass=java.lang.Boolean]"
                + "]]"));
  }
}

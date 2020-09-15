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
package schemacrawler.tools.executable;


import schemacrawler.schemacrawler.SchemaCrawlerException;

public final class CommandSchemaCrawlerCommand 
  extends BaseSchemaCrawlerCommand
{

  public CommandSchemaCrawlerCommand(final String command) throws SchemaCrawlerException
  {
    super(command);
  }

  @Override
  public void execute() throws Exception
  {
    checkCatalog();

    final String command = getCommand();

    final CommandRegistry commandRegistry =
        CommandRegistry.getCommandRegistry();
    final SchemaCrawlerCommand scCommand = commandRegistry
        .configureNewCommand(command, schemaCrawlerOptions,
            additionalConfiguration, outputOptions);
    if (scCommand == null)
    {
      throw new SchemaCrawlerException(
          "Could not configure command, " + command);
    }

    scCommand.setAdditionalConfiguration(additionalConfiguration);
    scCommand.setCatalog(catalog);
    scCommand.setConnection(connection);
    scCommand.setIdentifiers(identifiers);

    scCommand.initialize();
    scCommand.checkAvailability();
    scCommand.execute();

  }

  @Override
  public boolean usesConnection()
  {
    return false;
  }

}

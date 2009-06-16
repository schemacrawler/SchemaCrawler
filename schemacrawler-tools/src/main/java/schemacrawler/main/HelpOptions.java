/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.main;


import schemacrawler.Version;
import schemacrawler.schemacrawler.Options;

public class HelpOptions
  implements Options
{

  public enum CommandHelpType
  {
    complete, without_query, without_operations;
  }

  private static final long serialVersionUID = -2497570007150087268L;

  private final String title;
  private final String resourceApplicationOptions = "/help/ApplicationOptions.txt";
  private String resourceConnections = "/help/Connections.txt";
  private final String resourceCommands = "/help/Commands.%s.txt";
  private final String resourceConfig = "/help/Config.txt";
  private final String resourceSchemaCrawlerOptions = "/help/SchemaCrawlerOptions.txt";
  private CommandHelpType commandHelpType = CommandHelpType.complete;
  private String resourceOutputOptions = "/help/OutputOptions.txt";
  private boolean hideConfig;

  public HelpOptions(final String title)
  {
    this.title = title;
  }

  public CommandHelpType getCommandHelpType()
  {
    return commandHelpType;
  }

  public String getResourceConnections()
  {
    return resourceConnections;
  }

  public String getResourceOutputOptions()
  {
    return resourceOutputOptions;
  }

  public String getTitle()
  {
    return title;
  }

  public boolean isHideConfig()
  {
    return hideConfig;
  }

  public void setCommandHelpType(final CommandHelpType commandHelpType)
  {
    this.commandHelpType = commandHelpType;
  }

  public void setHideConfig(final boolean hideConfig)
  {
    this.hideConfig = hideConfig;
  }

  public void setResourceConnections(final String resourceConnections)
  {
    this.resourceConnections = resourceConnections;
  }

  public void setResourceOutputOptions(final String resourceOutputOptions)
  {
    this.resourceOutputOptions = resourceOutputOptions;
  }

  public void showHelp()
  {
    System.out.println(title);
    System.out.println(Version.about());
    System.out.println();

    showHelp(resourceConnections);
    showHelp(String.format(resourceCommands, commandHelpType));
    showHelp(resourceSchemaCrawlerOptions);
    if (!hideConfig)
    {
      showHelp(resourceConfig);
    }
    showHelp(resourceOutputOptions);
    showHelp(resourceApplicationOptions);
  }

  private void showHelp(final String helpResource)
  {
    if (schemacrawler.utility.Utility.isBlank(helpResource))
    {
      return;
    }
    final byte[] text = schemacrawler.utility.Utility
      .readFully(HelpOptions.class.getResourceAsStream(helpResource));
    System.out.println(new String(text));
  }

}

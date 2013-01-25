/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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

package schemacrawler.tools.commandline;


import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.Command;
import sf.util.Utility;
import sf.util.clparser.StringOption;

/**
 * Parses the command line.
 * 
 * @author Sualeh Fatehi
 */
final class CommandParser
  extends BaseOptionsParser<Command>
{

  CommandParser()
  {
    super(new StringOption('c', "command", null));
  }

  @Override
  protected Command getOptions()
    throws SchemaCrawlerException
  {
    if (hasOptions())
    {
      return new Command(getStringValue("command"));
    }
    else
    {
      throw new SchemaCrawlerException("No command specified");
    }
  }

  protected boolean hasOptions()
    throws SchemaCrawlerException
  {
    boolean hasOptions;
    if (hasOptionValue("command")
        && !Utility.isBlank(getStringValue("command")))
    {
      hasOptions = true;
    }
    else
    {
      hasOptions = false;
    }
    return hasOptions;
  }

}

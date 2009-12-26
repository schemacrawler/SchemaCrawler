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

package schemacrawler.tools.commandline;


import schemacrawler.schemacrawler.Options;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.CommandLineParser;
import sf.util.CommandLineParser.Option;

/**
 * Parses the command line.
 * 
 * @param <O>
 *        Options to be parsed from the command line.
 * @author Sualeh Fatehi
 */
public abstract class BaseOptionsParser<O extends Options>
{

  private final String[] args;

  protected BaseOptionsParser(final String[] args)
  {
    this.args = args;
  }

  /**
   * Parses the command line.
   * 
   * @return Command line options
   */
  protected abstract O getOptions()
    throws SchemaCrawlerException;

  protected void parse(final CommandLineParser.Option<?>[] options)
  {
    final CommandLineParser parser = new CommandLineParser();
    for (final Option<?> option: options)
    {
      parser.addOption(option);
    }
    parser.parse(args);
  }

}

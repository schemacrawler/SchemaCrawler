/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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


import schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.grep.GrepOptions;

/**
 * Utility for parsing the SchemaCrawler command line.
 * 
 * @author Sualeh Fatehi
 */
public class GrepCommandLine
  extends SchemaCrawlerCommandLine
{

  private final GrepOptions grepOptions;

  /**
   * Loads objects from command line options.
   * 
   * @param args
   *        Command line arguments.
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public GrepCommandLine(final String[] args)
    throws SchemaCrawlerException
  {
    this(args, null);
  }

  /**
   * Loads objects from command line options. Optionally loads the
   * config from the classpath.
   * 
   * @param args
   *        Command line arguments.
   * @param configResource
   *        Config resource.
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public GrepCommandLine(final String[] args, final String configResource)
    throws SchemaCrawlerException
  {
    super(args, configResource);

    if (args != null && args.length > 0)
    {
      grepOptions = new GrepOptionsParser(args).getValue();
    }
    else
    {
      grepOptions = new GrepOptions();
    }
  }

  /**
   * Gets the grep options.
   * 
   * @return Grep options.
   */
  public GrepOptions getGrepOptions()
  {
    return grepOptions;
  }

}

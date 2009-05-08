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

package schemacrawler.tools.integration.spring;


import schemacrawler.main.BaseOptionsParser;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;

/**
 * Options for the command line.
 * 
 * @author sfatehi
 */
final class SpringOptionsParser
  extends BaseOptionsParser<SpringOptions>
{

  private final StringOption optionContextFile = new StringOption('c',
                                                                  "context-file",
                                                                  "schemacrawler.context.xml");
  private final StringOption optionExecutable = new StringOption('x',
                                                                 "executable",
                                                                 "executable");
  private final StringOption optionDataSourceName = new StringOption('d',
                                                                     "datasource",
                                                                     "datasource");

  /**
   * Parses the command line into options.
   * 
   * @param args
   */
  public SpringOptionsParser(final String[] args)
  {
    super(args);
  }

  @Override
  protected String getHelpResource()
  {
    return "/help/Commands.readme.txt";
  }

  @Override
  protected SpringOptions getOptions()
  {
    parse(new Option[] {
        optionContextFile, optionExecutable, optionDataSourceName,
    });

    final SpringOptions options = new SpringOptions();
    options.setContextFileName(optionContextFile.getValue());
    options.setExecutableName(optionExecutable.getValue());
    options.setDataSourceName(optionDataSourceName.getValue());

    return options;
  }

}

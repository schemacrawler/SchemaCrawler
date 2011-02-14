/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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


import schemacrawler.tools.commandline.BaseOptionsParser;
import sf.util.clparser.StringOption;

/**
 * Options for the command line.
 * 
 * @author sfatehi
 */
final class SpringOptionsParser
  extends BaseOptionsParser<SpringOptions>
{

  /**
   * Parses the command line into options.
   * 
   * @param args
   *        Command-line arguments
   */
  SpringOptionsParser()
  {
    super(new StringOption('c', "context-file", "schemacrawler.context.xml"),
          new StringOption('x', "executable", "executable"),
          new StringOption('d', "datasource", "datasource"));
  }

  @Override
  protected SpringOptions getOptions()
  {
    final SpringOptions options = new SpringOptions();
    options.setContextFileName(getStringValue("c"));
    options.setExecutableName(getStringValue("x"));
    options.setDataSourceName(getStringValue("d"));

    return options;
  }

}

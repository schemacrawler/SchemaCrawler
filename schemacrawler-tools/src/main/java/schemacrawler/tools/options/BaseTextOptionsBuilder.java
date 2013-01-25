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

package schemacrawler.tools.options;


import schemacrawler.schemacrawler.Config;

public class BaseTextOptionsBuilder
{

  private final BaseTextOptions options;

  public BaseTextOptionsBuilder()
  {
    this(null);
  }

  public BaseTextOptionsBuilder(final Config config)
  {
    options = new BaseTextOptions(config)
    {

      private static final long serialVersionUID = 394786618097261880L;
    };
  }

  public BaseTextOptionsBuilder appendOutput()
  {
    options.setAppendOutput(true);
    return this;
  }

  public BaseTextOptionsBuilder hideFooter()
  {
    options.setNoFooter(true);
    return this;
  }

  public BaseTextOptionsBuilder hideHeader()
  {
    options.setNoHeader(true);
    return this;
  }

  public BaseTextOptionsBuilder hideInfo()
  {
    options.setNoInfo(true);
    return this;
  }

  public BaseTextOptionsBuilder overwriteOutput()
  {
    options.setAppendOutput(false);
    return this;
  }

  public BaseTextOptionsBuilder showFooter()
  {
    options.setNoFooter(false);
    return this;
  }

  public BaseTextOptionsBuilder showHeader()
  {
    options.setNoHeader(false);
    return this;
  }

  public BaseTextOptionsBuilder showInfo()
  {
    options.setNoInfo(false);
    return this;
  }

  public Config toConfig()
  {
    return options.toConfig();
  }

  @Override
  public String toString()
  {
    return options.toString();
  }
}

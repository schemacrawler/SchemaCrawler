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
package schemacrawler.tools.text.base;


import schemacrawler.schemacrawler.Config;

public final class CommonTextOptionsBuilder
  extends BaseTextOptionsBuilder<CommonTextOptionsBuilder, CommonTextOptions>
{

  public static CommonTextOptionsBuilder builder()
  {
    return new CommonTextOptionsBuilder();
  }

  public static CommonTextOptionsBuilder builder(final CommonTextOptions options)
  {
    return new CommonTextOptionsBuilder().fromOptions(options);
  }

  public static CommonTextOptions newCommonTextOptions()
  {
    return new CommonTextOptionsBuilder().toOptions();
  }

  public static CommonTextOptions newCommonTextOptions(final Config config)
  {
    return new CommonTextOptionsBuilder().fromConfig(config).toOptions();
  }

  private CommonTextOptionsBuilder()
  {
    // Set default values, if any
  }

  @Override
  public CommonTextOptions toOptions()
  {
    return new CommonTextOptions(this);
  }

}

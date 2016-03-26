/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.lint.executable;


import schemacrawler.tools.text.base.BaseTextOptions;

public class LintOptions
  extends BaseTextOptions
{

  private static final long serialVersionUID = -5917925090616219096L;

  private String linterConfigs;

  /**
   * Gets the path to the linter configs file.
   *
   * @return Path to the linter configs file.
   */
  public String getLinterConfigs()
  {
    return linterConfigs;
  }

  /**
   * Sets the path to the linter configs file.
   *
   * @param linterConfigs
   *        Path to the linter configs file.
   */
  public void setLinterConfigs(final String linterConfigs)
  {
    this.linterConfigs = linterConfigs;
  }

}

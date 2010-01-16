/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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


import schemacrawler.schemacrawler.Options;

/**
 * Additional options needed for Spring.
 * 
 * @author Sualeh Fatehi
 */
public class SpringOptions
  implements Options
{

  private static final long serialVersionUID = 5125868244511892692L;

  private String executableName;
  private String dataSourceName;
  private String contextFileName;

  /**
   * Spring context file name.
   * 
   * @return Spring context file name.
   */
  public String getContextFileName()
  {
    return contextFileName;
  }

  /**
   * Bean name for the datasource.
   * 
   * @return Bean name for the datasource.
   */
  public String getDataSourceName()
  {
    return dataSourceName;
  }

  /**
   * Bean name of the SchemaCrawler executable.
   * 
   * @return Bean name of the SchemaCrawler executable.
   */
  public String getExecutableName()
  {
    return executableName;
  }

  /**
   * Set the Spring context file name.
   * 
   * @param contextFileName
   *        Spring context file name.
   */
  public void setContextFileName(final String contextFileName)
  {
    this.contextFileName = contextFileName;
  }

  /**
   * Set the bean name for the datasource.
   * 
   * @param dataSourceName
   *        Bean name for the datasource.
   */
  public void setDataSourceName(final String dataSourceName)
  {
    this.dataSourceName = dataSourceName;
  }

  /**
   * Set the bean name of the SchemaCrawler executable.
   * 
   * @param executableName
   *        Bean name of the SchemaCrawler executable.
   */
  public void setExecutableName(final String executableName)
  {
    this.executableName = executableName;
  }

}

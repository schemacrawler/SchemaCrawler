/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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
  private String databaseSpecificOverrideOptionsName;
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
   * Bean name for the database-specific override options.
   *
   * @return Bean name for the database-specific override options.
   */
  public String getDatabaseSpecificOverrideOptionsName()
  {
    return databaseSpecificOverrideOptionsName;
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
   * Set the bean name for the database-specific override options.
   */
  public void setDatabaseSpecificOverrideOptionsName(final String databaseSpecificOverrideOptionsName)
  {
    this.databaseSpecificOverrideOptionsName = databaseSpecificOverrideOptionsName;
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

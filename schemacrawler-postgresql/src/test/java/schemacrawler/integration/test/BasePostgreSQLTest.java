/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.integration.test;


import static org.apache.commons.lang3.SystemUtils.IS_OS_MAC;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;
import static ru.yandex.qatools.embed.postgresql.distribution.Version.V10_6;
import static ru.yandex.qatools.embed.postgresql.distribution.Version.V11_1;

import ru.yandex.qatools.embed.postgresql.distribution.Version;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;

public class BasePostgreSQLTest
  extends BaseAdditionalDatabaseTest
{

  private static final boolean useProductionVersion = true;

  protected Version getEmbeddedPostgreSQLVersion()
  {
    if (useProductionVersion)
    {
      return V10_6;
    }
    else if (IS_OS_WINDOWS || IS_OS_MAC)
    {
      return V11_1;
    }
    else
    {
      return V10_6;
    }
  }

}

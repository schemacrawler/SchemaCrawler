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
package schemacrawler.server.oracle;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import schemacrawler.tools.databaseconnector.DatabaseConnectionUrlBuilder;

public class OracleUrlBuilder implements Supplier<DatabaseConnectionUrlBuilder>
{

  @Override
  public DatabaseConnectionUrlBuilder get()
  {
    final Map<String, String> map = new HashMap<>();
    map.put("remarksReporting", "true");
    map.put("restrictGetTables", "true");
    map.put("useFetchSizeWithLongColumn", "true");
    
    final DatabaseConnectionUrlBuilder urlBuilder = DatabaseConnectionUrlBuilder
        .builder("jdbc:oracle:thin:@//${host}:${port}/${database}")
        .withDefaultPort(1521).withDefaultUrlx(map);
    
    return urlBuilder;
  }

}

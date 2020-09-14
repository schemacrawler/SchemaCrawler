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
package schemacrawler.server.mysql;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import schemacrawler.tools.databaseconnector.DatabaseConnectionUrlBuilder;

public class MySQLUrlBuilder implements Supplier<DatabaseConnectionUrlBuilder>
{

  @Override
  public DatabaseConnectionUrlBuilder get()
  {
    final Map<String, String> map = new HashMap<>();
    map.put("nullNamePatternMatchesAll", "true");
    map.put("noAccessToProcedureBodies", "true");
    map.put("logger", "Jdk14Logger");
    map.put("dumpQueriesOnException", "true");
    map.put("dumpMetadataOnColumnNotFound", "true");
    map.put("maxQuerySizeToLog", "4096");
    map.put("disableMariaDbDriver", "true");
    map.put("useInformationSchema", "true");
    
    final DatabaseConnectionUrlBuilder urlBuilder = DatabaseConnectionUrlBuilder
        .builder("jdbc:mysql://${host}:${port}/${database}")
        .withDefaultPort(3306).withDefaultUrlx(map);
    
    return urlBuilder;
  }

}

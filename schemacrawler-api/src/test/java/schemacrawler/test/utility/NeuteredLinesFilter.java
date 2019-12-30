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
package schemacrawler.test.utility;


import java.util.function.Predicate;
import java.util.regex.Pattern;

final class NeuteredLinesFilter
  implements Predicate<String>
{

  private final Pattern[] neuters = {
    //
    Pattern.compile(".*jdbc:.*"),
    Pattern.compile("database product version.*"),
    Pattern.compile("driver version.*"),
    Pattern.compile("-- operating system:.*"),
    Pattern.compile("-- JVM system:.*"),
    Pattern.compile("\\s+<schemaCrawler(Version|About|Info)>.*"),
    Pattern.compile("\\s+\"runId\": .*"),
    Pattern.compile("\\s+<product(Name|Version)>.*"),
    Pattern.compile(".*[A-Za-z]+ \\d+\\, 201[456] \\d+:\\d+ [AP]M.*"),
    Pattern.compile(".*201[89]-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d.*"),
    Pattern.compile(".*201[89]-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d.*"),
    // JSON and YAML output
    Pattern.compile("- column @uuid: .*"),
    Pattern.compile("\\s+\"?run-id\"?\\s?: .*"),
    Pattern.compile("\\s+\"?crawl-timestamp\"?\\s?: .*"),
    Pattern.compile("\\s*(- )?\"?lint-id\"?\\s?: .*"),
    Pattern.compile("\\s+\"?linter-instance-id\"?\\s?: .*"),
    // Versions
    Pattern.compile(".*15\\.0[6-7]\\.\\d\\d.*"),
    Pattern.compile(".*16\\.\\d\\.\\d.*"),
    // Operating systems and environment
    Pattern.compile(".*(Windows|Linux).*"),
    Pattern.compile(".*(Java|OpenJDK).*"),
    // SQL Server
    // -- server-specific values
    Pattern.compile(".*ServerName.*"),
    // DB2
    // -- server-specific values
    Pattern.compile(".*HOST_NAME.*"),
    Pattern.compile(".*TOTAL_MEMORY.*"),
    Pattern.compile(".*TOTAL_CPUS.*"),
    Pattern.compile(".*OS_NAME.*"),
    // Apache Derby
    // -- unnamed objects
    Pattern.compile("SQL\\d+\\s+\\[primary key\\]"),
    Pattern.compile("SQL\\d+\\s+\\[foreign key, with no action\\]"),
    // MySQL
    // -- server-specific values
    Pattern.compile("server_uuid\\s+.*"),
    Pattern.compile("hostname\\s+.*"),
    Pattern.compile("  value\\s+\\d+\\s+"),
    // SQL Server
    // -- unnamed objects
    Pattern.compile("PK__Publishe__3214EC07.*\\s+\\[primary key\\]"),
    // Oracle
    // -- server-specific values
    Pattern.compile("\\s+value\\s+localhost:\\d+:xe\\s+"),
    };

  /**
   * Should we keep the line - that is, not ignore it?
   */
  @Override
  public boolean test(final String line)
  {
    for (final Pattern neuter : neuters)
    {
      if (neuter
        .matcher(line)
        .matches())
      {
        return false;
      }
    }
    return true;
  }

}

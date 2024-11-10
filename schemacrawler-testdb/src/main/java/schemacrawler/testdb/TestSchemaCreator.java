/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.testdb;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import static java.util.Objects.requireNonNull;
import us.fatehi.utility.SQLRuntimeException;
import us.fatehi.utility.database.SqlScript;
import us.fatehi.utility.ioresource.ClasspathInputResource;

public class TestSchemaCreator implements Runnable {

  public static void executeScriptLine(
      final String scriptResourceLine, final Connection connection) {

    requireNonNull(scriptResourceLine, "No script resource line provided");
    requireNonNull(connection, "No database connection provided");

    final String scriptResource;
    final String delimiter;

    final String[] split = scriptResourceLine.split(",");
    if (split.length == 1) {
      scriptResource = scriptResourceLine.trim();
      if (scriptResource.isEmpty()) {
        delimiter = "#";
      } else {
        delimiter = ";";
      }
    } else if (split.length == 2) {
      delimiter = split[0].trim();
      scriptResource = split[1].trim();
    } else {
      throw new SQLRuntimeException(String.format("Too many fields in \"%s\"", scriptResourceLine));
    }

    final boolean skip = "#".equals(delimiter);
    if (skip) {
      return;
    }

    try (final BufferedReader scriptReader =
        new BufferedReader(
            new ClasspathInputResource(scriptResource).openNewInputReader(UTF_8)); ) {
      new SqlScript(scriptReader, delimiter, connection).run();
    } catch (final IOException e) {
      throw new SQLRuntimeException(String.format("Could not read \"%s\"", scriptResource), e);
    }
  }

  private final Connection connection;

  private final String scriptsResource;

  public TestSchemaCreator(final Connection connection, final String scriptsResource) {
    this.connection = requireNonNull(connection, "No database connection provided");
    this.scriptsResource = requireNonNull(scriptsResource, "No script resource provided");
  }

  @Override
  public void run() {
    try (final BufferedReader reader =
        new BufferedReader(new ClasspathInputResource(scriptsResource).openNewInputReader(UTF_8))) {
      reader.lines().forEach(line -> executeScriptLine(line, connection));
    } catch (final IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}

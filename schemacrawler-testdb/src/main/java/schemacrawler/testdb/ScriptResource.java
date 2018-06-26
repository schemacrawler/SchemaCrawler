/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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
import java.io.InputStreamReader;
import java.io.Reader;

public final class ScriptResource
{

  private final String scriptName;
  private final String delimiter;
  private final boolean skip;

  public ScriptResource(final String scriptResourceLine)
  {
    if (scriptResourceLine == null || scriptResourceLine.trim().isEmpty())
    {
      skip = true;
      delimiter = "";
      scriptName = "";
    }
    else if (scriptResourceLine.startsWith("#"))
    {
      skip = true;
      delimiter = "";
      scriptName = scriptResourceLine.substring(1);
    }
    else if (scriptResourceLine.startsWith("~"))
    {
      skip = false;
      delimiter = "@";
      scriptName = scriptResourceLine.substring(1);
    }
    else
    {
      skip = false;
      delimiter = ";";
      scriptName = scriptResourceLine;
    }
  }

  public String getDelimiter()
  {
    return delimiter;
  }

  public String getScriptName()
  {
    return scriptName;
  }

  public BufferedReader openReader()
    throws IOException
  {
    if (skip)
    {
      throw new IOException("Cannot open reader for skipped script");
    }
    final Reader reader = new InputStreamReader(TestSchemaCreator.class
      .getResourceAsStream(scriptName), UTF_8);
    return new BufferedReader(reader);
  }

  public boolean skip()
  {
    return skip;
  }

  @Override
  public String toString()
  {
    return getScriptName();
  }

}

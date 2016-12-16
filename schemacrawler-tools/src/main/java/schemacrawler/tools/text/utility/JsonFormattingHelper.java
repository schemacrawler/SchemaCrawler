/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.text.utility;


import java.io.PrintWriter;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.utility.org.json.JSONException;
import schemacrawler.tools.text.utility.org.json.JSONObject;

public class JsonFormattingHelper
  extends PlainTextFormattingHelper
{

  public JsonFormattingHelper(final PrintWriter out,
                              final TextOutputFormat outputFormat)
  {
    super(out, outputFormat);
  }

  public void write(final JSONObject jsonObject)
    throws SchemaCrawlerException
  {
    try
    {
      jsonObject.write(out, 2);
    }
    catch (final JSONException e)
    {
      throw new SchemaCrawlerException("Could not write database", e);
    }
  }

}

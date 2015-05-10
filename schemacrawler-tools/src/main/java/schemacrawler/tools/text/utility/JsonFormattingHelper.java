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

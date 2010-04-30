package schemacrawler.tools.integration.graph;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import sf.util.Utility;

public class StreamReaderTask
  extends FutureTask<String>
{

  private static final class StreamReader
    implements Callable<String>
  {

    private final InputStream in;

    StreamReader(final InputStream in)
    {
      this.in = in;
    }

    public String call()
      throws Exception
    {
      final Reader reader = new BufferedReader(new InputStreamReader(in));
      return Utility.readFully(reader);
    }
  }

  public StreamReaderTask(final InputStream in)
  {
    super(new StreamReader(in));
  }

}

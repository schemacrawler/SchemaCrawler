package schemacrawler.test;


import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import schemacrawler.Version;

public class TestVersion
{
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

  @After
  public void cleanUpStreams()
  {
    System.setOut(null);
    System.setErr(null);
  }

  @Before
  public void setUpStreams()
  {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }

  @Test
  public void version()
  {
    Version.main(new String[0]);
    assertTrue(outContent.toString().startsWith("SchemaCrawler 14.05.01"));
  }

}

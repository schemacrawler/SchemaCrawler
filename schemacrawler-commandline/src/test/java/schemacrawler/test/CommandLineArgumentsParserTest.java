package schemacrawler.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import sf.util.commandlineparser.CommandLineArgumentsParser;

public class CommandLineArgumentsParserTest
{

  @Test
  public void mixed1()
  {
    final String[] args = new String[] {
        "-blah", "value with spaces", "othernonoption"
    };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertEquals(1, optionsMap.size());
    assertFalse(optionsMap.containsKey("random"));
    assertTrue(optionsMap.containsKey("blah"));
    assertEquals("value with spaces", optionsMap.get("blah"));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertEquals(1, nonOptionArguments.size());
    assertFalse(nonOptionArguments.contains("random"));
    assertTrue(nonOptionArguments.contains("othernonoption"));
  }

  @Test
  public void mixed2()
  {
    final String[] args = new String[] {
        "-blah", "value with spaces", "othernonoption", "-other option"
    };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertEquals(2, optionsMap.size());
    assertFalse(optionsMap.containsKey("random"));
    assertTrue(optionsMap.containsKey("blah"));
    assertEquals("value with spaces", optionsMap.get("blah"));
    assertTrue(optionsMap.containsKey("other option"));
    assertEquals(null, optionsMap.get("other option"));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertEquals(1, nonOptionArguments.size());
    assertFalse(nonOptionArguments.contains("random"));
    assertTrue(nonOptionArguments.contains("othernonoption"));
  }

  @Test
  public void nonOptionArguments1()
  {
    final String[] args = new String[] {
      "nonoption with spaces"
    };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertEquals(0, optionsMap.size());

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertEquals(1, nonOptionArguments.size());
    assertFalse(nonOptionArguments.contains("random"));
    assertTrue(nonOptionArguments.contains("nonoption with spaces"));
  }

  @Test
  public void nonOptionArguments2()
  {
    final String[] args = new String[] {
        "nonoption with spaces", "othernonoption"
    };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertEquals(0, optionsMap.size());

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertEquals(2, nonOptionArguments.size());
    assertFalse(nonOptionArguments.contains("random"));
    assertTrue(nonOptionArguments.contains("nonoption with spaces"));
    assertTrue(nonOptionArguments.contains("othernonoption"));
  }

  @Test
  public void noOption()
  {
    final String[] args = new String[] {
      "-=blah"
    };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertEquals(1, optionsMap.size());
    assertFalse(optionsMap.containsKey("random"));
    assertTrue(optionsMap.containsKey(""));
    assertEquals("blah", optionsMap.get(""));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertEquals(0, nonOptionArguments.size());
    assertFalse(nonOptionArguments.contains("random"));
  }

  @Test
  public void noOptionOrValue()
  {
    final String[] args = new String[] {
      "-"
    };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertEquals(1, optionsMap.size());
    assertFalse(optionsMap.containsKey("random"));
    assertTrue(optionsMap.containsKey(""));
    assertEquals(null, optionsMap.get(""));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertEquals(0, nonOptionArguments.size());
    assertFalse(nonOptionArguments.contains("random"));
  }

  @Test
  public void noOptionOrValueButImplied()
  {
    final String[] args = new String[] {
      "-="
    };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertEquals(1, optionsMap.size());
    assertFalse(optionsMap.containsKey("random"));
    assertTrue(optionsMap.containsKey(""));
    assertEquals(null, optionsMap.get(""));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertEquals(0, nonOptionArguments.size());
    assertFalse(nonOptionArguments.contains("random"));
  }

  @Test
  public void noValue()
  {
    final String[] args = new String[] {
      "-blah"
    };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertEquals(1, optionsMap.size());
    assertFalse(optionsMap.containsKey("random"));
    assertTrue(optionsMap.containsKey("blah"));
    assertEquals(null, optionsMap.get("blah"));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertEquals(0, nonOptionArguments.size());
    assertFalse(nonOptionArguments.contains("random"));
  }

  @Test
  public void noValue2()
  {
    final String[] args = new String[] {
        "-blah", "-foo"
    };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertEquals(2, optionsMap.size());
    assertFalse(optionsMap.containsKey("random"));
    assertTrue(optionsMap.containsKey("blah"));
    assertEquals(null, optionsMap.get("blah"));
    assertTrue(optionsMap.containsKey("foo"));
    assertEquals(null, optionsMap.get("foo"));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertEquals(0, nonOptionArguments.size());
    assertFalse(nonOptionArguments.contains("random"));
  }

  @Test
  public void noValueButImplied()
  {
    final String[] args = new String[] {
      "-blah="
    };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertEquals(1, optionsMap.size());
    assertFalse(optionsMap.containsKey("random"));
    assertTrue(optionsMap.containsKey("blah"));
    assertEquals(null, optionsMap.get("blah"));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertEquals(0, nonOptionArguments.size());
    assertFalse(nonOptionArguments.contains("random"));
  }

  @Test
  public void optionAndValue()
  {
    final String[] args = new String[] {
      "-blah=3"
    };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertEquals(1, optionsMap.size());
    assertFalse(optionsMap.containsKey("random"));
    assertTrue(optionsMap.containsKey("blah"));
    assertEquals("3", optionsMap.get("blah"));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertEquals(0, nonOptionArguments.size());
    assertFalse(nonOptionArguments.contains("random"));
  }

  @Test
  public void repeatedOption()
  {
    final String[] args = new String[] {
        "-blah=4", "-blah=3"
    };

    final CommandLineArgumentsParser parser = new CommandLineArgumentsParser(args);
    parser.parse();

    final Map<String, String> optionsMap = parser.getOptionsMap();
    assertEquals(1, optionsMap.size());
    assertFalse(optionsMap.containsKey("random"));
    assertTrue(optionsMap.containsKey("blah"));
    assertEquals("3", optionsMap.get("blah"));

    final List<String> nonOptionArguments = parser.getNonOptionArguments();
    assertEquals(0, nonOptionArguments.size());
    assertFalse(nonOptionArguments.contains("random"));
  }

}

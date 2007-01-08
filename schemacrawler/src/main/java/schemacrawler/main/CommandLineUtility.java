package schemacrawler.main;


import java.util.Properties;

import schemacrawler.Grep;
import schemacrawler.Version;
import sf.util.CommandLineParser;
import sf.util.Utilities;

public class CommandLineUtility
{

  private CommandLineUtility()
  {
    // Prevent instantiation
  }

  public static void checkForHelp(final String[] args, final String helpResource)
  {
    boolean printUsage = false;
    if (args.length == 0)
    {
      printUsage = true;
    }
    for (int i = 0; i < args.length; i++)
    {
      final String arg = args[i];
      if (arg.equalsIgnoreCase("-help") || arg.equalsIgnoreCase("-?")
          || arg.equalsIgnoreCase("--help"))
      {
        printUsage = true;
        break;
      }
    }
    if (printUsage)
    {
      final byte[] text = Utilities.readFully(Grep.class
        .getResourceAsStream(helpResource));
      final String info = new String(text);

      System.out.println(Version.about());
      System.out.println(info);
      System.exit(0);
    }
  }

  public static boolean getBooleanOption(final CommandLineParser.BaseOption option)
  {
    if (option == null || option.getValue() == null)
    {
      return false;
    }
    return Boolean.valueOf(option.getValue().toString()).booleanValue();
  }

  public static String getStringOption(final CommandLineParser.BaseOption option,
                                       final String defaultValue)
  {
    if (option == null || option.getValue() == null)
    {
      return defaultValue;
    }
    return option.getValue().toString();
  }

  public static Properties loadConfig(final String configfilename,
                                      final String configoverridefilename)
  {
    Properties config = new Properties();
    config = Utilities.loadProperties(config, configfilename);
    config = Utilities.loadProperties(config, configoverridefilename);
    return config;
  }

}

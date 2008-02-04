package schemacrawler;


import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper used to assemble the classpath before launching the actual
 * application. The big trick is assembling the classpath before the
 * launch. Yep, this is functionality that should be built right into
 * Java.
 * <p>
 * See
 * {@link http://tapestryjava.blogspot.com/2007/08/quick-and-dirty-java-application.html}
 */
public final class LauncherMain
{

  private static final List<URL> _classpath = new ArrayList<URL>();

  /**
   * Usage:
   * 
   * <pre>
   * java -jar mblauncher.jar some.class.to.launch [--addclasspath dir] [--addjardir dir] [options]
   * </pre>
   * 
   * <p>
   * The --addclasspath parameter is used to add a directory to add to
   * the classpath. This is most commonly used with a directory
   * containing configuration files that override corresponding files
   * stored inside other JARs on the classpath.
   * <p>
   * The --addjardir parameter is used to define directories. JAR files
   * directly within such directories will be added to the search path.
   * <p>
   * Any remaining options will be collected and passed to the main()
   * method of the launch class.
   */
  public static void main(final String[] args)
  {

    final List<String> launchOptions = new ArrayList<String>();

    if (args.length == 0)
    {
      fail("No class to launch was specified.  This should be the first parameter.");
    }

    final String launchClass = args[0];

    int cursor = 1;

    while (cursor < args.length)
    {

      final String arg = args[cursor];

      if (arg.equals("--addclasspath"))
      {
        if (cursor + 1 == args.length)
        {
          fail("--addclasspath argument was not followed by the name of the directory to add to the classpath.");
        }

        final String dir = args[cursor + 1];

        add(dir);

        cursor += 2;
        continue;
      }

      if (arg.equals("--addjardir"))
      {

        if (cursor + 1 == args.length)
        {
          fail("--addjardir argument was not followed by the name of a directory to search for JARs.");
        }

        final String dir = args[cursor + 1];

        search(dir);

        cursor += 2;
        continue;
      }

      launchOptions.add(arg);

      cursor++;
    }

    final String[] newArgs = launchOptions.toArray(new String[launchOptions
      .size()]);

    launch(launchClass, newArgs);
  }

  private static void add(final String directoryName)
  {
    final File dir = toDir(directoryName);

    if (dir == null)
    {
      return;
    }

    addToClasspath(dir);
  }

  private static void addToClasspath(final File jar)
  {
    final URL url = toURL(jar);

    if (url != null)
    {
      _classpath.add(url);
    }
  }

  private static void fail(final String message)
  {
    System.err.println("Launcher failure: " + message);

    System.exit(-1);
  }

  private static void launch(final String launchClassName, final String[] args)
  {

    final URL[] classpathURLs = _classpath.toArray(new URL[_classpath.size()]);

    try
    {
      final URLClassLoader newLoader = new URLClassLoader(classpathURLs, Thread
        .currentThread().getContextClassLoader());

      Thread.currentThread().setContextClassLoader(newLoader);

      final Class launchClass = newLoader.loadClass(launchClassName);

      final Method main = launchClass.getMethod("main", new Class[] {
        String[].class
      });

      main.invoke(null, new Object[] {
        args
      });
    }
    catch (final ClassNotFoundException ex)
    {
      fail(String.format("Class '%s' not found.", launchClassName));
    }
    catch (final NoSuchMethodException ex)
    {
      fail(String.format("Class '%s' does not contain a main() method.",
                         launchClassName));
    }
    catch (final Exception ex)
    {
      fail(String.format("Error invoking method main() of %s: %s",
                         launchClassName,
                         ex.toString()));
    }
  }

  private static void search(final String directoryName)
  {

    final File dir = toDir(directoryName);

    if (dir == null)
    {
      return;
    }

    final File[] jars = dir.listFiles(new FilenameFilter()
    {

      public boolean accept(File dir, String name)
      {
        return name.endsWith(".jar");
      }

    });

    for (final File jar: jars)
    {
      addToClasspath(jar);
    }

  }

  private static File toDir(final String directoryName)
  {
    final File dir = new File(directoryName);

    if (!dir.exists())
    {
      System.err.printf("Warning: directory '%s' does not exist.\n",
                        directoryName);
      return null;
    }

    if (!dir.isDirectory())
    {
      System.err.printf("Warning: '%s' is a file, not a directory.\n",
                        directoryName);
      return null;
    }

    return dir;
  }

  private static URL toURL(final File file)
  {
    try
    {
      return file.toURL();
    }
    catch (final MalformedURLException ex)
    {
      System.err.printf("Error converting %s to a URL: %s\n", file, ex
        .getMessage());

      return null;
    }
  }

}

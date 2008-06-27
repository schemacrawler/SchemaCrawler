/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package schemacrawler;


import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import sf.util.CommandLineUtility;

/**
 * A wrapper used to assemble the classpath before launching the actual
 * application.
 * <p>
 * See <a href=" http://tapestryjava.blogspot.com/2007/08/quick-and-dirty-java-application.html"
 * >launcher discussion</a>.
 */
public final class LauncherMain
{

  private static final Logger LOGGER = Logger.getLogger(LauncherMain.class
    .getName());

  private static final String LIB_DIR = "./lib";

  /**
   * Loads all jars files in the ./lib directory, and then launches the
   * specified class file with arguments, in a new classloader.
   * 
   * @param args
   *        Command line arguments
   */
  public static void main(final String[] args)
  {

    CommandLineUtility.setLogLevel(args);

    if (args.length == 0)
    {
      fail("No class to launch was specified.");
    }
    final String launchClass = args[0];

    // Load jars from the lib directory
    final List<URL> classpath = search(LIB_DIR);

    final List<String> launchOptions = new ArrayList<String>();
    if (args.length > 1)
    {
      for (int i = 1; i < args.length; i++)
      {
        launchOptions.add(args[i]);
      }
    }
    final String[] launchArgs = launchOptions.toArray(new String[launchOptions
      .size()]);

    launch(classpath, launchClass, launchArgs);

  }

  private static void addJarFileToClasspath(final File jarFile,
                                            final List<URL> classpath)
  {
    final URL url = toURL(jarFile);

    if (url != null)
    {
      LOGGER.log(Level.INFO, "Adding " + jarFile);
      classpath.add(url);
    }
  }

  private static void fail(final String message)
  {
    fail(message, null);
  }

  private static void fail(final String message, final Exception e)
  {
    if (e != null)
    {
      LOGGER.log(Level.SEVERE, message, e);
    }
    else
    {
      LOGGER.log(Level.SEVERE, message);
    }
    System.exit(-1);
  }

  private static void launch(final List<URL> classpath,
                             final String launchClassName,
                             final String[] args)
  {
    try
    {
      final URL[] classpathURLs = classpath.toArray(new URL[classpath.size()]);
      final URLClassLoader newLoader = new URLClassLoader(classpathURLs, null);

      Thread.currentThread().setContextClassLoader(newLoader);

      LOGGER.log(Level.INFO, "Lauching " + launchClassName + " -"
                             + Arrays.toString(args));
      final Class<?> launchClass = newLoader.loadClass(launchClassName);
      final Method main = launchClass.getMethod("main", new Class[] {
        String[].class
      });

      main.invoke(null, new Object[] {
        args
      });
    }
    catch (final ClassNotFoundException ex)
    {
      fail(String.format("Class '%s' not found", launchClassName));
    }
    catch (final NoSuchMethodException ex)
    {
      fail(String.format("Class '%s' does not contain a main() method",
                         launchClassName));
    }
    catch (final Exception e)
    {
      fail(String.format("Error invoking method main() of %s", launchClassName),
           e);
    }
  }

  private static List<URL> search(final String directoryName)
  {
    final List<URL> classpath = new ArrayList<URL>();
    final File dir = toDir(directoryName);
    if (dir != null)
    {
      final File[] jarFiles = dir.listFiles(new FilenameFilter()
      {
        public boolean accept(final File dir, final String name)
        {
          return name.endsWith(".jar");
        }
      });

      for (final File jarFile: jarFiles)
      {
        addJarFileToClasspath(jarFile, classpath);
      }
    }
    return classpath;

  }

  private static File toDir(final String directoryName)
  {
    final File dir = new File(directoryName);

    if (!dir.exists())
    {
      LOGGER.log(Level.WARNING, String.format("Directory %s does not exist",
                                              directoryName));
      return null;
    }

    if (!dir.isDirectory())
    {
      LOGGER.log(Level.WARNING, String.format("%s is not a directory",
                                              directoryName));
      return null;
    }

    return dir;
  }

  private static URL toURL(final File file)
  {
    try
    {
      return file.toURI().toURL();
    }
    catch (final MalformedURLException ex)
    {
      LOGGER.log(Level.WARNING, String.format("Cannot convert %s to a URL",
                                              file), ex);
      return null;
    }
  }

  private LauncherMain()
  {
    // Prevent instantiation
  }

}

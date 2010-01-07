package schemacrawler.test;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @see <a
 *      href="http://stackoverflow.com/questions/1839671/finding-installed-jdbc-drivers"
 *      >Finding Installed JDBC Drivers</a>
 */
public class ImplementationFinder
{

  private static final Logger LOGGER = Logger
    .getLogger(ImplementationFinder.class.getName());

  private static final String JAR_FILE_PATTERN = ".+\\.jar$";

  public static void main(final String[] args)
    throws Exception
  {
    final List<Class<Driver>> drivers = new ImplementationFinder(Driver.class)
      .findImplementations();
    for (final Class<Driver> driver: drivers)
    {
      System.out.println(driver.getName());
    }
  }

  private final Class<?> baseClass;

  public ImplementationFinder(final Class<?> baseClass)
  {
    if (baseClass == null)
    {
      throw new IllegalArgumentException("No interface class provided");
    }
    this.baseClass = baseClass;
  }

  public <T extends Object> List<Class<T>> findImplementations()
    throws IOException
  {
    final List<Class<T>> classes = new ArrayList<Class<T>>();
    for (final File classpathResource: findClasspathResources())
    {
      if (classpathResource.getName().matches(JAR_FILE_PATTERN))
      {
        searchInJar(classpathResource, classes);
      }
      else
      {
        for (final File file: findFiles(classpathResource))
        {
          searchInJar(file, classes);
        }
      }
    }

    return classes;
  }

  private Collection<File> findClasspathResources()
    throws IOException
  {
    final Set<File> classpathResources = new HashSet<File>();
    final ClassLoader classLoader = Thread.currentThread()
      .getContextClassLoader();
    for (final URL root: Collections.list(classLoader.getResources("")))
    {
      LOGGER.log(Level.FINER, "Classloader resource " + root);
      final File file = new File(root.getFile());
      final File fileDecoded = new File(URLDecoder.decode(file
        .getAbsolutePath(), "UTF-8"));
      if (fileDecoded.canRead())
      {
        LOGGER.log(Level.FINER, "Added resource " + fileDecoded);
        classpathResources.add(fileDecoded);
      }
    }
    final String systemClassPath = System.getProperty("java.class.path");
    final String[] systemClassPathResources = systemClassPath.split(System
      .getProperty("path.separator"));
    for (final String systemClassPathResource: systemClassPathResources)
    {
      LOGGER.log(Level.FINER, "Classpath resource " + systemClassPathResource);
      final File file = new File(systemClassPathResource);
      if (file.canRead())
      {
        final File resourceFile = new File(file.getAbsolutePath());
        LOGGER.log(Level.FINER, "Added resource " + resourceFile);
        classpathResources.add(resourceFile);
      }
    }
    return classpathResources;
  }

  private List<File> findFiles(final File directory)
    throws IOException
  {
    if (directory == null || !directory.isDirectory())
    {
      return Collections.EMPTY_LIST;
    }

    final File[] files = directory.listFiles(new FileFilter()
    {
      public boolean accept(final File file)
      {
        return file.getName().matches(JAR_FILE_PATTERN);
      }
    });
    if (files == null)
    {
      return Collections.EMPTY_LIST;
    }

    final List<File> found = new ArrayList<File>(files.length);
    for (final File file: files)
    {
      if (!file.isDirectory() && file.canRead())
      {
        found.add(file);
      }
    }
    return found;
  }

  private <T extends Object> void searchInJar(final File file,
                                              final List<Class<T>> classes)
    throws IOException
  {
    final JarFile jarFile = new JarFile(file);
    for (final JarEntry jarEntry: Collections.list(jarFile.entries()))
    {
      final String name = jarEntry.getName();
      if (name.endsWith(".class"))
      {
        try
        {
          final Class<?> found = Class.forName(name.replace("/", ".")
            .replaceAll("\\.class$", ""));
          if (baseClass.isAssignableFrom(found))
          {
            classes.add((Class<T>) found);
          }
        }
        catch (final Throwable ignore)
        {
          // No real class file, or JAR not in classpath, or missing
          // links.
        }
      }
    }
  }

}

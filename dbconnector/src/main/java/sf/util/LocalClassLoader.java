/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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

package sf.util;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This classloader can be used to dynamically load classes. The parent class
 * loader will be used first when loading a class. As long as the class for the
 * object being returned is in the parent classloader, the caller will be able
 * to receive the returned object.
 */
public final class LocalClassLoader
  extends URLClassLoader
{

  private static final Logger LOGGER = Logger.getLogger(LocalClassLoader.class
    .getName());

  /**
   * Constructor. <p/> Create a new LocalClassLoader given the urls and parent
   * ClassLoader.
   * 
   * @param urls
   *          The url's to use.
   * @param parent
   *          The parent ClassLoader to be used.
   */
  private LocalClassLoader(final URL[] urls, final ClassLoader parent)
  {
    super(urls, parent);
  }

  /**
   * This method should be used to get the LocalClassLoader.
   * 
   * @return LocalClassLoader instance
   */
  public static LocalClassLoader getClassLoader()
  {
    return getClassLoader(new String[] {
      "."
    });
  }

  /**
   * This method should be used to get the LocalClassLoader.
   * 
   * @param paths
   *          The paths from where to load the jars, zips etc.
   * @return New LocalClassLoader
   */
  public static LocalClassLoader getClassLoader(final String[] paths)
  {
    final URLClassLoader sysLoader = (URLClassLoader) ClassLoader
      .getSystemClassLoader();
    final LocalClassLoader loader = new LocalClassLoader(sysLoader.getURLs(),
        LocalClassLoader.class.getClassLoader());

    if (paths != null && paths.length > 0)
    {
      for (int i = 0; i < paths.length; i++)
      {
        final String path = paths[i];

        if (path != null && path.length() > 0)
        {
          final File f = new File(path);

          if (f.exists())
          {
            if (f.isDirectory())
            {
              loader.addDirToClasspath(path);
              loader.addAllArchivesToClasspath(path);
            }
            else
            {
              loader.addArchiveToClasspath(path);
            }
          }
        }
      }
    }

    return loader;
  }

  /**
   * Adds all of the jar and zip archives in the specified directory to the
   * classpath. Only the files ending with .jar and .zip will be used.
   * 
   * @param dirPath
   *          Path to the directory.
   */
  public void addAllArchivesToClasspath(final String dirPath)
  {
    final File wd = new File(dirPath);
    final String[] files = wd.list();

    if (files != null)
    {
      for (int i = 0; i < files.length; i++)
      {
        final String file = files[i];

        if (file.endsWith(".jar") || file.endsWith(".zip"))
        {
          final File f = new File(dirPath + File.separator + file);

          try
          {
            final URL url = new URL("file:" + f.getCanonicalPath());

            addURL(url);
          }
          catch (final MalformedURLException e)
          {
            LOGGER.log(Level.WARNING, "", e);
          }
          catch (final IOException e)
          {
            LOGGER.log(Level.WARNING, "", e);
          }
        }
      }
    }
  }

  /**
   * Adds the archive (.zip or .jar) file to classpath.
   * 
   * @param filePath
   *          The path to the archive file.
   */
  public void addArchiveToClasspath(final String filePath)
  {
    if (filePath.endsWith(".jar") || filePath.endsWith(".zip"))
    {
      final File f = new File(filePath);

      try
      {
        final URL url = new URL("file:" + f.getCanonicalPath());

        addURL(url);
      }
      catch (final MalformedURLException e)
      {
        LOGGER.log(Level.WARNING, "", e);
      }
      catch (final IOException e)
      {
        LOGGER.log(Level.WARNING, "", e);
      }
    }
  }

  /**
   * Adds a directory to the classpath.
   * 
   * @param dirPath
   *          The path to the directory to be added.
   */
  public void addDirToClasspath(final String dirPath)
  {
    try
    {
      final URL url = new URL("file:///" + dirPath + "/");

      addURL(url);
    }
    catch (final MalformedURLException e)
    {
      LOGGER.log(Level.WARNING, "", e);
    }
  }
}

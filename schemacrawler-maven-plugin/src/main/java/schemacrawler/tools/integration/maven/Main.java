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

package schemacrawler.tools.integration.maven;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import schemacrawler.Version;
import sf.util.Utilities;

/**
 * Main class that takes arguments for a database for crawling a schema.
 */
public final class Main
{

  private static final String VERIFY_MAVEN_PLUGIN_SCRIPT_FILESTEM = "verify-schemacrawler-maven-plugin";
  private static final String INSTALL_MAVEN_PLUGIN_SCRIPT_FILESTEM = "install-schemacrawler-maven-plugin";
  private static final String MAVEN_PLUGIN_POM_FILENAME = "schemacrawler-maven-plugin.pom";
  private static final String MAVEN_PLUGIN_INSTRUCTONS_FILENAME = "schemacrawler-maven-plugin.txt";

  /**
   * Internal storage for information. Read from text file.
   */
  private static String pluginPom;
  /**
   * Internal storage for information. Read from text file.
   */
  private static String instructions;

  static
  {
    byte[] text;
    //
    text = Utilities.readFully(Main.class
      .getResourceAsStream("/" + MAVEN_PLUGIN_POM_FILENAME));
    pluginPom = new String(text);
    //
    text = Utilities.readFully(Main.class
      .getResourceAsStream("/" + MAVEN_PLUGIN_INSTRUCTONS_FILENAME));
    instructions = new String(text);
  }

  /**
   * Get connection parameters, and creates a connection, and crawls the
   * schema.
   * 
   * @param args
   *        Arguments passed into the program from the command line.
   * @throws Exception
   *         On an exception
   */
  public static void main(final String[] args)
    throws Exception
  {

    final String shellExt = isWindowsOS()? ".cmd": ".sh";

    System.out.println(Version.about());

    // Create POM file
    final File pomFile = writeStringToFile(MAVEN_PLUGIN_POM_FILENAME, pluginPom);
    System.out.println("Created Maven POM file: " + pomFile.getAbsolutePath());

    // Create install script
    final String installScript = "mvn install:install-file "
                                 + "-DgroupId=schemacrawler "
                                 + "-DartifactId=schemacrawler-maven-plugin "
                                 + "-Dversion=" + Version.getVersion() + " "
                                 + "-Dpackaging=maven-plugin "
                                 + "-Dfile=schemacrawler-"
                                 + Version.getVersion() + ".jar "
                                 + "-DpomFile=schemacrawler-maven-plugin.pom ";
    final File installScriptFile = writeStringToFile(INSTALL_MAVEN_PLUGIN_SCRIPT_FILESTEM
                                                         + shellExt,
                                                     installScript);
    System.out.println("Created installation script: "
                       + installScriptFile.getAbsolutePath());

    // Create verify script
    final String verifyScript = "mvn help:describe "
                                + "-DgroupId=schemacrawler "
                                + "-DartifactId=schemacrawler-maven-plugin "
                                + "-Dversion=" + Version.getVersion() + " ";
    final File verifyScriptFile = writeStringToFile(VERIFY_MAVEN_PLUGIN_SCRIPT_FILESTEM
                                                        + shellExt,
                                                    verifyScript);
    System.out.println("Created verification script: "
                       + verifyScriptFile.getAbsolutePath());

    // Instructions
    System.out.println(instructions);

  }

  /**
   * Returns true if the current operating system is Windows.
   * 
   * @return True is the current operating system is Windows.
   */
  public static boolean isWindowsOS()
  {
    final String osName = System.getProperty("os.name");
    final boolean isWindowsOS = osName == null
                                || osName.toLowerCase().indexOf("windows") != -1;
    return isWindowsOS;
  }

  /**
   * Writes a string to a file.
   * 
   * @param fileName
   *        Name of the file to write.
   * @param fileContents
   *        Contents of the file.
   * @return The file.
   * @throws IOException
   *         On an exception.
   */
  public static File writeStringToFile(final String fileName,
                                       final String fileContents)
    throws IOException
  {
    FileWriter writer = null;
    try
    {
      final File file = new File(fileName);
      writer = new FileWriter(file);
      writer.write(fileContents);
      writer.flush();
      return file;
    }
    finally
    {
      if (writer != null)
      {
        writer.close();
      }
    }
  }

  private Main()
  {
  }

}

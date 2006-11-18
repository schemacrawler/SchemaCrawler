/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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

  private Main()
  {
  }

  /**
   * Get connection parameters, and creates a connection, and crawls the
   * schema.<BR>
   * 
   * @param args
   *          Arguments passed into the program from the command line.
   * @throws Exception
   *           On an exception
   */
  public static void main(final String[] args)
    throws Exception
  {

    String shellExt = Utilities.isWindowsOS()? ".cmd": ".sh";

    System.out.println(Version.about());

    // Create POM file
    File pomFile = Utilities.writeStringToFile(MAVEN_PLUGIN_POM_FILENAME,
                                               pluginPom);
    System.out.println("Created Maven POM file: " + pomFile.getAbsolutePath());

    // Create install script
    String installScript = "mvn install:install-file "
                           + "-DgroupId=schemacrawler "
                           + "-DartifactId=schemacrawler-maven-plugin "
                           + "-Dversion=" + Version.getVersion() + " "
                           + "-Dpackaging=maven-plugin "
                           + "-Dfile=schemacrawler-" + Version.getVersion()
                           + ".jar "
                           + "-DpomFile=schemacrawler-maven-plugin.pom ";
    File installScriptFile = Utilities.writeStringToFile(
                                                         INSTALL_MAVEN_PLUGIN_SCRIPT_FILESTEM
                                                             + shellExt,
                                                         installScript);
    System.out.println("Created installation script: "
                       + installScriptFile.getAbsolutePath());

    // Create verify script
    String verifyScript = "mvn help:describe " + "-DgroupId=schemacrawler "
                          + "-DartifactId=schemacrawler-maven-plugin "
                          + "-Dversion=" + Version.getVersion() + " ";
    File verifyScriptFile = Utilities.writeStringToFile(
                                                        VERIFY_MAVEN_PLUGIN_SCRIPT_FILESTEM
                                                            + shellExt,
                                                        verifyScript);
    System.out.println("Created verification script: "
                       + verifyScriptFile.getAbsolutePath());

    // Instructions
    System.out.println(instructions);

  }

}

/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.commandline.shell;


import picocli.CommandLine;
import schemacrawler.JvmSystemInfo;
import schemacrawler.OperatingSystemInfo;
import schemacrawler.SchemaCrawlerInfo;

@CommandLine.Command(name = "version",
                     aliases = {
                       "sys-info", "system-info"
                     },
                     header = "** System Information Options - Display SchemaCrawler version and system information")
public class SystemCommand
  implements Runnable
{

  @CommandLine.Option(names = { "-V", "--version" },
                      versionHelp = true,
                      description = "Display SchemaCrawler version and system information")
  private boolean versionRequested;

  public boolean isVersionRequested()
  {
    return versionRequested;
  }

  @Override
  public void run()
  {
    final SchemaCrawlerInfo scInfo = new SchemaCrawlerInfo();
    System.out.println(scInfo.getSchemaCrawlerAbout());

    System.out.println("System Information:");
    final OperatingSystemInfo osInfo = new OperatingSystemInfo();
    System.out.println(osInfo);
    final JvmSystemInfo jvmInfo = new JvmSystemInfo();
    System.out.println(jvmInfo);

    new AvailableCommandsCommand().run();

    new AvailableServersCommand().run();

  }

}

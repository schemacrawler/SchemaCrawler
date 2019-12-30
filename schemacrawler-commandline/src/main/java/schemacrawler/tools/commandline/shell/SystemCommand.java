/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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


import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import schemacrawler.JvmSystemInfo;
import schemacrawler.OperatingSystemInfo;
import schemacrawler.SchemaCrawlerInfo;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import schemacrawler.tools.commandline.state.StateUtility;

@Command(name = "system", aliases = {
  "sys", "sys-info"
}, header = "** Display SchemaCrawler version and system information", headerHeading = "", synopsisHeading = "Shell Command:%n", customSynopsis = {
  "system"
}, optionListHeading = "Options:%n")
public class SystemCommand
  implements Runnable
{

  private final SchemaCrawlerShellState state;

  @Option(names = "--is-connected", description = "Checks whether the shell has a connection to a database")
  private boolean isconnected;
  @Option(names = "--is-loaded", description = "Checks whether the shell has loaded database metadata")
  private boolean isloaded;
  @Option(names = "--show-stacktrace", description = "Shows stack trace from previous command")
  private boolean showstacktrace;
  @Option(names = "--show-state", description = "Shows internal state")
  private boolean showstate;
  @Option(names = {
    "-V", "--version"
  }, description = "Display SchemaCrawler version and system information")
  private boolean versionRequested;

  public SystemCommand(final SchemaCrawlerShellState state)
  {
    this.state = state;
  }

  public boolean isVersionRequested()
  {
    return versionRequested;
  }

  @Override
  public void run()
  {
    if (versionRequested)
    {
      printVersion();
    }
    if (isconnected)
    {
      final boolean isConnectedState = state.isConnected();
      System.out.println(String.format("%sonnected to the database",
                                       isConnectedState? "C": "Not c"));
    }
    if (isloaded)
    {
      final boolean isLoadedState = state.isLoaded();
      System.out.println(String.format("Database metadata is %sloaded",
                                       isLoadedState? "": "not "));
    }
    if (showstacktrace)
    {
      final Throwable lastExceptionState = state.getLastException();
      if (lastExceptionState != null)
      {
        lastExceptionState.printStackTrace(System.out);
      }
    }
    if (showstate)
    {
      StateUtility.logState(state, true);
    }
  }

  public void printVersion()
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

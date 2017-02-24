/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.integration.spring;


import static java.nio.file.Files.exists;
import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.commandline.CommandLine;
import schemacrawler.tools.executable.Executable;
import sf.util.StringFormat;

public class SchemaCrawlerSpringCommandLine
  implements CommandLine
{

  private static final Logger LOGGER = Logger
    .getLogger(SchemaCrawlerSpringCommandLine.class.getName());

  private final SpringOptions springOptions;

  public SchemaCrawlerSpringCommandLine(final Config argsMap)
    throws SchemaCrawlerException
  {
    requireNonNull(argsMap, "No command-line arguments provided");
    final SpringOptionsParser springOptionsParser = new SpringOptionsParser(argsMap);
    springOptions = springOptionsParser.getOptions();
  }

  @Override
  public void execute()
    throws Exception
  {
    final Path contextFile = Paths.get(springOptions.getContextFileName())
      .normalize().toAbsolutePath();
    final ApplicationContext appContext;
    if (exists(contextFile))
    {
      final String contextFilePath = contextFile.toUri().toString();
      LOGGER.log(Level.INFO,
                 new StringFormat("Loading context from file <%s>",
                                  contextFilePath));
      appContext = new FileSystemXmlApplicationContext(contextFilePath);
    }
    else
    {
      LOGGER.log(Level.INFO,
                 new StringFormat("Loading context from classpath <%s>",
                                  springOptions.getContextFileName()));
      appContext = new ClassPathXmlApplicationContext(springOptions
        .getContextFileName());
    }

    try
    {
      final DataSource dataSource = (DataSource) appContext
        .getBean(springOptions.getDataSourceName());
      final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions = (DatabaseSpecificOverrideOptions) appContext
        .getBean(springOptions.getDatabaseSpecificOverrideOptionsName());

      try (Connection connection = dataSource.getConnection();)
      {
        final Executable executable = (Executable) appContext
          .getBean(springOptions.getExecutableName());
        if (databaseSpecificOverrideOptions == null)
        {
          executable.execute(connection);
        }
        else
        {
          executable.execute(connection, databaseSpecificOverrideOptions);
        }
      }
    }
    finally
    {
      ((AbstractXmlApplicationContext) appContext).close();
    }
  }

}

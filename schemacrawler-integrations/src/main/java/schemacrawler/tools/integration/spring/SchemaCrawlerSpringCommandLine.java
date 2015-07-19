/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
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
import sf.util.commandlineparser.CommandLineUtility;

public class SchemaCrawlerSpringCommandLine
  implements CommandLine
{

  private static final Logger LOGGER = Logger
    .getLogger(SchemaCrawlerSpringCommandLine.class.getName());

  private final SpringOptions springOptions;

  public SchemaCrawlerSpringCommandLine(final String[] args)
    throws SchemaCrawlerException
  {
    requireNonNull(args);

    final Config config = CommandLineUtility.loadConfig(args);

    final SpringOptionsParser springOptionsParser = new SpringOptionsParser(config);
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
      LOGGER.log(Level.INFO, "Loading context from file, " + contextFilePath);
      appContext = new FileSystemXmlApplicationContext(contextFilePath);
    }
    else
    {
      LOGGER.log(Level.INFO, "Loading context from classpath, "
                             + springOptions.getContextFileName());
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

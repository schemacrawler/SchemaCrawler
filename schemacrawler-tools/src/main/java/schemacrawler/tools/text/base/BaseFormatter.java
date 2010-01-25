/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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

package schemacrawler.tools.text.base;


import java.io.PrintWriter;
import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.DatabaseProperty;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.JdbcDriverProperty;
import schemacrawler.schema.SchemaCrawlerInfo;
import schemacrawler.schemacrawler.Options;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.util.HtmlFormattingHelper;
import schemacrawler.tools.text.util.PlainTextFormattingHelper;
import schemacrawler.tools.text.util.TextFormattingHelper;
import schemacrawler.tools.text.util.TextFormattingHelper.DocumentHeaderType;
import sf.util.ObjectToString;

/**
 * Text formatting of schema.
 * 
 * @author Sualeh Fatehi
 */
public abstract class BaseFormatter<O extends Options>
{

  protected final O options;
  protected final OutputOptions outputOptions;
  protected final PrintWriter out;
  protected final TextFormattingHelper formattingHelper;
  private final boolean printVerboseDatabaseInfo;

  protected BaseFormatter(final O options,
                          final boolean printVerboseDatabaseInfo,
                          final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    if (options == null)
    {
      throw new IllegalArgumentException("Options not provided");
    }
    this.options = options;

    this.printVerboseDatabaseInfo = printVerboseDatabaseInfo;

    if (outputOptions == null)
    {
      throw new IllegalArgumentException("Output options not provided");
    }
    this.outputOptions = outputOptions;
    final OutputFormat outputFormat = outputOptions.getOutputFormat();
    if (outputFormat == OutputFormat.html)
    {
      formattingHelper = new HtmlFormattingHelper(outputFormat);
    }
    else
    {
      formattingHelper = new PlainTextFormattingHelper(outputFormat);
    }

    out = outputOptions.openOutputWriter();
  }

  public void handle(final SchemaCrawlerInfo schemaCrawlerInfo,
                     final DatabaseInfo databaseInfo,
                     final JdbcDriverInfo jdbcDriverInfo)
    throws SchemaCrawlerException
  {
    if (outputOptions.isNoInfo())
    {
      return;
    }
    handle(schemaCrawlerInfo);
    handle(databaseInfo);
    handle(jdbcDriverInfo);
  }

  private void handle(final DatabaseInfo dbInfo)
  {
    if (dbInfo == null)
    {
      return;
    }

    out.println(formattingHelper.createHeader(DocumentHeaderType.section,
                                              "Database Information"));

    out.print(formattingHelper.createObjectStart(""));
    out.println(formattingHelper.createNameValueRow("database product name",
                                                    dbInfo.getProductName()));
    out
      .println(formattingHelper.createNameValueRow("database product version",
                                                   dbInfo.getProductVersion()));
    out.println(formattingHelper.createNameValueRow("database user name",
                                                    dbInfo.getUserName()));
    out.print(formattingHelper.createObjectEnd());

    if (printVerboseDatabaseInfo && dbInfo.getProperties().length > 0)
    {
      out.println(formattingHelper.createHeader(DocumentHeaderType.section,
                                                "Database Characteristics"));
      out.print(formattingHelper.createObjectStart(""));
      for (final DatabaseProperty property: dbInfo.getProperties())
      {
        final String name = property.getDescription();
        Object value = property.getValue();
        if (value == null)
        {
          value = "";
        }
        out.println(formattingHelper.createNameValueRow(name, ObjectToString
          .toString(value)));
      }
      out.print(formattingHelper.createObjectEnd());
    }

    out.flush();
  }

  private void handle(final JdbcDriverInfo driverInfo)
  {
    if (driverInfo == null)
    {
      return;
    }

    out.println(formattingHelper.createHeader(DocumentHeaderType.section,
                                              "JDBC Driver Information"));

    out.print(formattingHelper.createObjectStart(""));
    out.println(formattingHelper.createNameValueRow("driver name", driverInfo
      .getDriverName()));
    out.println(formattingHelper.createNameValueRow("driver version",
                                                    driverInfo
                                                      .getDriverVersion()));
    out.println(formattingHelper.createNameValueRow("driver class name",
                                                    driverInfo
                                                      .getDriverClassName()));
    out.println(formattingHelper.createNameValueRow("url", driverInfo
      .getConnectionUrl()));
    out.println(formattingHelper.createNameValueRow("is JDBC compliant",
                                                    Boolean.toString(driverInfo
                                                      .isJdbcCompliant())));
    out.print(formattingHelper.createObjectEnd());

    final JdbcDriverProperty[] jdbcDriverProperties = driverInfo
      .getDriverProperties();
    if (printVerboseDatabaseInfo && jdbcDriverProperties.length > 0)
    {
      out.println(formattingHelper.createHeader(DocumentHeaderType.section,
                                                "JDBC Driver Properties"));
      for (final JdbcDriverProperty driverProperty: jdbcDriverProperties)
      {
        out.print(formattingHelper.createObjectStart(""));
        printJdbcDriverProperty(driverProperty);
        out.print(formattingHelper.createObjectEnd());
      }
    }

    out.flush();
  }

  private final void handle(final SchemaCrawlerInfo schemaCrawlerInfo)
  {
    if (schemaCrawlerInfo == null)
    {
      return;
    }

    out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                              "System Information"));

    out.println(formattingHelper.createHeader(DocumentHeaderType.section,
                                              "SchemaCrawler Information"));

    out.print(formattingHelper.createObjectStart(""));
    out.println(formattingHelper
      .createNameValueRow("product name", schemaCrawlerInfo
        .getSchemaCrawlerProductName()));
    out.println(formattingHelper
      .createNameValueRow("product version", schemaCrawlerInfo
        .getSchemaCrawlerVersion()));
    out.print(formattingHelper.createObjectEnd());

    if (printVerboseDatabaseInfo)
    {
      final SortedMap<String, String> systemProperties = new TreeMap<String, String>(schemaCrawlerInfo
        .getSystemProperties());
      if (!systemProperties.isEmpty())
      {
        out.println(formattingHelper.createHeader(DocumentHeaderType.section,
                                                  "System Properties"));
        out.print(formattingHelper.createObjectStart(""));
        for (final Entry<String, String> systemProperty: systemProperties
          .entrySet())
        {
          out.println(formattingHelper.createNameValueRow(systemProperty
            .getKey(), systemProperty.getValue()));
        }
        out.print(formattingHelper.createObjectEnd());
      }
    }

    out.flush();
  }

  private void printJdbcDriverProperty(final JdbcDriverProperty driverProperty)
  {
    final String choices = Arrays.asList(driverProperty.getChoices())
      .toString();
    final String required = (driverProperty.isRequired()? "": "not ")
                            + "required";
    String details = required;
    if (driverProperty.getChoices() != null
        && driverProperty.getChoices().length > 0)
    {
      details = details + "; choices " + choices;
    }
    final String value = driverProperty.getValue();

    out.println(formattingHelper.createNameRow(driverProperty.getName(),
                                               "[driver property]",
                                               false));
    out.println(formattingHelper.createDefinitionRow(driverProperty
      .getDescription()));
    out.println(formattingHelper.createDefinitionRow(details));
    out.println(formattingHelper.createDetailRow("", "value", value));
  }

}

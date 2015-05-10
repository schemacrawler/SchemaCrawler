/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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


import static sf.util.Utility.isBlank;

import java.util.Collection;

import schemacrawler.schema.CrawlHeaderInfo;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.DatabaseProperty;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.JdbcDriverProperty;
import schemacrawler.schema.SchemaCrawlerInfo;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.utility.TextFormattingHelper.DocumentHeaderType;
import schemacrawler.tools.text.utility.html.Alignment;
import sf.util.ObjectToString;

/**
 * Text formatting of schema.
 *
 * @author Sualeh Fatehi
 */
public abstract class BaseTabularFormatter<O extends BaseTextOptions>
  extends BaseFormatter<O>
{

  protected BaseTabularFormatter(final O options,
                                 final boolean printVerboseDatabaseInfo,
                                 final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    super(options, printVerboseDatabaseInfo, outputOptions);
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.DataTraversalHandler#begin()
   */
  @Override
  public void begin()
  {
    if (!options.isNoHeader())
    {
      formattingHelper.writeDocumentStart();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#end()
   */
  @Override
  public void end()
    throws SchemaCrawlerException
  {
    if (!options.isNoFooter())
    {
      formattingHelper.writeDocumentEnd();
    }

    super.end();
  }

  @Override
  public void handle(final CrawlHeaderInfo crawlHeaderInfo)
  {
    if (crawlHeaderInfo == null)
    {
      return;
    }

    final String title = crawlHeaderInfo.getTitle();
    if (!isBlank(title))
    {
      formattingHelper.writeHeader(DocumentHeaderType.title, title);
    }

    if (options.isNoInfo())
    {
      return;
    }

    formattingHelper.writeHeader(DocumentHeaderType.subTitle,
                                  "System Information");

    formattingHelper.writeObjectStart();
    formattingHelper.writeNameValueRow("generated by",
                                        crawlHeaderInfo.getSchemaCrawlerInfo(),
                                        Alignment.inherit);
    formattingHelper.writeNameValueRow("generated on",
                                        formatTimestamp(crawlHeaderInfo
                                          .getCrawlTimestamp()),
                                        Alignment.inherit);
    formattingHelper.writeNameValueRow("database version",
                                        crawlHeaderInfo.getDatabaseInfo(),
                                        Alignment.inherit);
    formattingHelper.writeNameValueRow("driver version",
                                        crawlHeaderInfo.getJdbcDriverInfo(),
                                        Alignment.inherit);
    formattingHelper.writeObjectEnd();
  }

  @Override
  public final void handle(final DatabaseInfo dbInfo)
  {
    if (!printVerboseDatabaseInfo || options.isNoInfo() || dbInfo == null)
    {
      return;
    }

    formattingHelper.writeHeader(DocumentHeaderType.section,
                                  "Database Information");

    formattingHelper.writeObjectStart();
    formattingHelper.writeNameValueRow("database product name",
                                        dbInfo.getProductName(),
                                        Alignment.inherit);
    formattingHelper.writeNameValueRow("database product version",
                                        dbInfo.getProductVersion(),
                                        Alignment.inherit);
    formattingHelper.writeNameValueRow("database user name",
                                        dbInfo.getUserName(),
                                        Alignment.inherit);
    formattingHelper.writeObjectEnd();

    if (dbInfo.getProperties().size() > 0)
    {
      formattingHelper.writeHeader(DocumentHeaderType.section,
                                    "Database Characteristics");
      formattingHelper.writeObjectStart();
      for (final DatabaseProperty property: dbInfo.getProperties())
      {
        final String name = property.getDescription();
        Object value = property.getValue();
        if (value == null)
        {
          value = "";
        }
        formattingHelper.writeNameValueRow(name,
                                            ObjectToString.toString(value),
                                            Alignment.inherit);
      }
      formattingHelper.writeObjectEnd();
    }
  }

  @Override
  public void handle(final JdbcDriverInfo driverInfo)
  {
    if (!printVerboseDatabaseInfo || options.isNoInfo() || driverInfo == null)
    {
      return;
    }

    formattingHelper.writeHeader(DocumentHeaderType.section,
                                  "JDBC Driver Information");

    formattingHelper.writeObjectStart();
    formattingHelper.writeNameValueRow("driver name",
                                        driverInfo.getDriverName(),
                                        Alignment.inherit);
    formattingHelper.writeNameValueRow("driver version",
                                        driverInfo.getDriverVersion(),
                                        Alignment.inherit);
    formattingHelper.writeNameValueRow("driver class name",
                                        driverInfo.getDriverClassName(),
                                        Alignment.inherit);
    formattingHelper.writeNameValueRow("url",
                                        driverInfo.getConnectionUrl(),
                                        Alignment.inherit);
    formattingHelper.writeNameValueRow("is JDBC compliant", Boolean
      .toString(driverInfo.isJdbcCompliant()), Alignment.inherit);
    formattingHelper.writeObjectEnd();

    final Collection<JdbcDriverProperty> jdbcDriverProperties = driverInfo
      .getDriverProperties();
    if (jdbcDriverProperties.size() > 0)
    {
      formattingHelper.writeHeader(DocumentHeaderType.section,
                                    "JDBC Driver Properties");
      for (final JdbcDriverProperty driverProperty: jdbcDriverProperties)
      {
        formattingHelper.writeObjectStart();
        printJdbcDriverProperty(driverProperty);
        formattingHelper.writeObjectEnd();
      }
    }
  }

  @Override
  public void handle(final SchemaCrawlerInfo schemaCrawlerInfo)
  {
    if (!printVerboseDatabaseInfo || options.isNoInfo()
        || schemaCrawlerInfo == null)
    {
      return;
    }

    formattingHelper.writeHeader(DocumentHeaderType.section,
                                  "SchemaCrawler Information");

    formattingHelper.writeObjectStart();
    formattingHelper.writeNameValueRow("product name", schemaCrawlerInfo
      .getSchemaCrawlerProductName(), Alignment.inherit);
    formattingHelper.writeNameValueRow("product version", schemaCrawlerInfo
      .getSchemaCrawlerVersion(), Alignment.inherit);
    formattingHelper.writeObjectEnd();
  }

  @Override
  public final void handleHeaderEnd()
    throws SchemaCrawlerException
  {

  }

  @Override
  public final void handleHeaderStart()
    throws SchemaCrawlerException
  {

  }

  @Override
  public final void handleInfoEnd()
    throws SchemaCrawlerException
  {

  }

  @Override
  public final void handleInfoStart()
    throws SchemaCrawlerException
  {
    if (!printVerboseDatabaseInfo || options.isNoInfo())
    {
      return;
    }

    formattingHelper.writeHeader(DocumentHeaderType.subTitle,
                                  "System Information");
  }

  private void printJdbcDriverProperty(final JdbcDriverProperty driverProperty)
  {
    final String required = (driverProperty.isRequired()? "": "not ")
                            + "required";
    String details = required;
    if (driverProperty.getChoices() != null
        && driverProperty.getChoices().size() > 0)
    {
      details = details + "; choices " + driverProperty.getChoices();
    }
    final String value = driverProperty.getValue();

    formattingHelper.writeNameRow(driverProperty.getName(),
                                   "[driver property]");
    formattingHelper.writeDescriptionRow(driverProperty.getDescription());
    formattingHelper.writeDescriptionRow(details);
    formattingHelper.writeDetailRow("", "value", value);
  }
}

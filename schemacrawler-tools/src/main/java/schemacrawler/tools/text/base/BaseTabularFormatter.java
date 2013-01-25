/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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


import java.util.Collection;

import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.DatabaseProperty;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.JdbcDriverProperty;
import schemacrawler.schema.SchemaCrawlerInfo;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.utility.Alignment;
import schemacrawler.tools.text.utility.TextFormattingHelper.DocumentHeaderType;
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
      out.println(formattingHelper.createDocumentStart());
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
      out.println(formattingHelper.createDocumentEnd());
    }

    out.close();
  }

  @Override
  public final void handle(final DatabaseInfo dbInfo)
  {
    if (options.isNoInfo() || dbInfo == null)
    {
      return;
    }

    out.println(formattingHelper.createHeader(DocumentHeaderType.section,
                                              "Database Information"));

    out.print(formattingHelper.createObjectStart(""));
    out.println(formattingHelper.createNameValueRow("database product name",
                                                    dbInfo.getProductName(),
                                                    Alignment.left));
    out.println(formattingHelper.createNameValueRow("database product version",
                                                    dbInfo.getProductVersion(),
                                                    Alignment.left));
    out.println(formattingHelper.createNameValueRow("database user name",
                                                    dbInfo.getUserName(),
                                                    Alignment.left));
    out.print(formattingHelper.createObjectEnd());

    if (printVerboseDatabaseInfo && dbInfo.getProperties().size() > 0)
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
          .toString(value), Alignment.left));
      }
      out.print(formattingHelper.createObjectEnd());
    }

    out.flush();
  }

  @Override
  public void handle(final JdbcDriverInfo driverInfo)
  {
    if (options.isNoInfo() || driverInfo == null)
    {
      return;
    }

    out.println(formattingHelper.createHeader(DocumentHeaderType.section,
                                              "JDBC Driver Information"));

    out.print(formattingHelper.createObjectStart(""));
    out.println(formattingHelper.createNameValueRow("driver name",
                                                    driverInfo.getDriverName(),
                                                    Alignment.left));
    out.println(formattingHelper.createNameValueRow("driver version",
                                                    driverInfo
                                                      .getDriverVersion(),
                                                    Alignment.left));
    out.println(formattingHelper.createNameValueRow("driver class name",
                                                    driverInfo
                                                      .getDriverClassName(),
                                                    Alignment.left));
    out.println(formattingHelper.createNameValueRow("url", driverInfo
      .getConnectionUrl(), Alignment.left));
    out.println(formattingHelper.createNameValueRow("is JDBC compliant",
                                                    Boolean.toString(driverInfo
                                                      .isJdbcCompliant()),
                                                    Alignment.left));
    out.print(formattingHelper.createObjectEnd());

    final Collection<JdbcDriverProperty> jdbcDriverProperties = driverInfo
      .getDriverProperties();
    if (printVerboseDatabaseInfo && jdbcDriverProperties.size() > 0)
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

  @Override
  public void handle(final SchemaCrawlerInfo schemaCrawlerInfo)
  {
    if (options.isNoInfo() || schemaCrawlerInfo == null)
    {
      return;
    }

    out.println(formattingHelper.createHeader(DocumentHeaderType.section,
                                              "SchemaCrawler Information"));

    out.print(formattingHelper.createObjectStart(""));
    out.println(formattingHelper
      .createNameValueRow("product name",
                          schemaCrawlerInfo.getSchemaCrawlerProductName(),
                          Alignment.left));
    out.println(formattingHelper
      .createNameValueRow("product version",
                          schemaCrawlerInfo.getSchemaCrawlerVersion(),
                          Alignment.left));
    out.print(formattingHelper.createObjectEnd());

    out.flush();
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
    if (options.isNoInfo())
    {
      return;
    }

    out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                              "System Information"));
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

    out.println(formattingHelper.createNameRow(driverProperty.getName(),
                                               "[driver property]",
                                               false));
    out.println(formattingHelper.createDescriptionRow(driverProperty
      .getDescription()));
    out.println(formattingHelper.createDescriptionRow(details));
    out.println(formattingHelper.createDetailRow("", "value", value));
  }

}

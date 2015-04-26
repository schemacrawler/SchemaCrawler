/*
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
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

package schemacrawler.tools.text.schema;


import static sf.util.Utility.isBlank;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.CrawlHeaderInfo;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.Routine;
import schemacrawler.schema.SchemaCrawlerInfo;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.base.BaseFormatter;
import schemacrawler.tools.text.utility.TextFormattingHelper.DocumentHeaderType;
import schemacrawler.tools.text.utility.html.Alignment;
import schemacrawler.tools.traversal.SchemaTraversalHandler;

/**
 * Text formatting of schema.
 *
 * @author Sualeh Fatehi
 */
final class SchemaListFormatter
  extends BaseFormatter<SchemaTextOptions>
  implements SchemaTraversalHandler
{

  private final boolean isVerbose;

  /**
   * Text formatting of schema.
   *
   * @param schemaTextDetailType
   *        Types for text formatting of schema
   * @param options
   *        Options for text formatting of schema
   * @param outputOptions
   *        Options for text formatting of schema
   * @throws SchemaCrawlerException
   *         On an exception
   */
  SchemaListFormatter(final SchemaTextDetailType schemaTextDetailType,
                      final SchemaTextOptions options,
                      final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    super(options,
          schemaTextDetailType == SchemaTextDetailType.details,
          outputOptions);
    isVerbose = schemaTextDetailType == SchemaTextDetailType.details;
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

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handle(schemacrawler.schema.ColumnDataType)
   */
  @Override
  public void handle(final ColumnDataType columnDataType)
    throws SchemaCrawlerException
  {
    if (printVerboseDatabaseInfo && isVerbose)
    {
      final String databaseSpecificTypeName;
      if (options.isShowUnqualifiedNames())
      {
        databaseSpecificTypeName = columnDataType.getName();
      }
      else
      {
        databaseSpecificTypeName = columnDataType.getFullName();
      }
      out.println(formattingHelper.createNameRow(databaseSpecificTypeName,
                                                 "[data type]"));
    }
  }

  @Override
  public final void handle(final DatabaseInfo dbInfo)
  {
  }

  @Override
  public void handle(final JdbcDriverInfo driverInfo)
  {
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handle(Routine)
   */
  @Override
  public void handle(final Routine routine)
  {
    final String routineTypeDetail = String.format("%s, %s",
                                                   routine.getRoutineType(),
                                                   routine.getReturnType());
    final String routineName;
    if (options.isShowUnqualifiedNames())
    {
      routineName = routine.getName();
    }
    else
    {
      routineName = routine.getFullName();
    }
    final String routineType = "[" + routineTypeDetail + "]";

    out.println(formattingHelper.createNameRow(routineName, routineType));
    printRemarks(routine);

    out.flush();

  }

  @Override
  public void handle(final CrawlHeaderInfo crawlHeaderInfo)
  {
    if (options.isNoInfo() || crawlHeaderInfo == null)
    {
      return;
    }

    final String title = crawlHeaderInfo.getTitle();
    out.println(formattingHelper
      .createHeader(DocumentHeaderType.section,
                    isBlank(title)? "System Information": title));

    out.print(formattingHelper.createObjectStart());
    out.println(formattingHelper.createNameValueRow("generated by",
                                                    crawlHeaderInfo
                                                      .getSchemaCrawlerInfo(),
                                                    Alignment.inherit));
    out.println(formattingHelper
      .createNameValueRow("generated on",
                          formatTimestamp(crawlHeaderInfo.getCrawlTimestamp()),
                          Alignment.inherit));
    out.println(formattingHelper.createNameValueRow("database version",
                                                    crawlHeaderInfo
                                                      .getDatabaseInfo(),
                                                    Alignment.inherit));
    out.println(formattingHelper.createNameValueRow("driver version",
                                                    crawlHeaderInfo
                                                      .getJdbcDriverInfo(),
                                                    Alignment.inherit));
    out.print(formattingHelper.createObjectEnd());

    out.flush();
  }

  @Override
  public void handle(final SchemaCrawlerInfo schemaCrawlerInfo)
  {
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handle(schemacrawler.schema.Sequence)
   */
  @Override
  public void handle(final Sequence sequence)
  {
    final String sequenceName;
    if (options.isShowUnqualifiedNames())
    {
      sequenceName = sequence.getName();
    }
    else
    {
      sequenceName = sequence.getFullName();
    }
    final String sequenceType = "[sequence]";

    out.println(formattingHelper.createNameRow(sequenceName, sequenceType));
    printRemarks(sequence);

    out.flush();

  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handle(schemacrawler.schema.Synonym)
   */
  @Override
  public void handle(final Synonym synonym)
  {
    final String synonymName;
    if (options.isShowUnqualifiedNames())
    {
      synonymName = synonym.getName();
    }
    else
    {
      synonymName = synonym.getFullName();
    }
    final String synonymType = "[synonym]";

    out.println(formattingHelper.createNameRow(synonymName, synonymType));
    printRemarks(synonym);

    out.flush();

  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handle(schemacrawler.schema.Table)
   */
  @Override
  public void handle(final Table table)
  {
    final String tableName;
    if (options.isShowUnqualifiedNames())
    {
      tableName = table.getName();
    }
    else
    {
      tableName = table.getFullName();
    }
    final String tableType = "[" + table.getTableType() + "]";

    out.println(formattingHelper.createNameRow(tableName, tableType));
    printRemarks(table);

    out.flush();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleColumnDataTypesEnd()
   */
  @Override
  public void handleColumnDataTypesEnd()
  {
    if (printVerboseDatabaseInfo && isVerbose)
    {
      out.append(formattingHelper.createObjectEnd());
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleColumnDataTypesStart()
   */
  @Override
  public void handleColumnDataTypesStart()
  {
    if (printVerboseDatabaseInfo && isVerbose)
    {
      out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                                "Data Types"));

      out.append(formattingHelper.createObjectStart());
    }
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
    if (options.isNoInfo())
    {
      return;
    }

    out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                              "Database Information"));
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
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleRoutinesEnd()
   */
  @Override
  public void handleRoutinesEnd()
    throws SchemaCrawlerException
  {
    out.println(formattingHelper.createObjectEnd());
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleRoutinesStart()
   */
  @Override
  public void handleRoutinesStart()
    throws SchemaCrawlerException
  {
    out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                              "Routines"));

    out.println(formattingHelper.createObjectStart());
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleSequencesEnd()
   */
  @Override
  public void handleSequencesEnd()
    throws SchemaCrawlerException
  {
    out.println(formattingHelper.createObjectEnd());
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleSequencesStart()
   */
  @Override
  public void handleSequencesStart()
    throws SchemaCrawlerException
  {
    out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                              "Sequences"));

    out.println(formattingHelper.createObjectStart());
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleSynonymsEnd()
   */
  @Override
  public void handleSynonymsEnd()
    throws SchemaCrawlerException
  {
    out.println(formattingHelper.createObjectEnd());
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleSynonymsStart()
   */
  @Override
  public void handleSynonymsStart()
    throws SchemaCrawlerException
  {
    out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                              "Synonyms"));

    out.println(formattingHelper.createObjectStart());
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleTablesEnd()
   */
  @Override
  public void handleTablesEnd()
    throws SchemaCrawlerException
  {
    out.append(formattingHelper.createObjectEnd());
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.SchemaTraversalHandler#handleTablesStart()
   */
  @Override
  public void handleTablesStart()
    throws SchemaCrawlerException
  {
    out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                              "Tables"));

    out.println(formattingHelper.createObjectStart());
  }

  private void printRemarks(final DatabaseObject object)
  {
    if (object == null || !object.hasRemarks())
    {
      return;
    }
    out.println(formattingHelper.createDescriptionRow(object.getRemarks()));
  }

}

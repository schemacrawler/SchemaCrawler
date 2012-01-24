/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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
package schemacrawler.tools.lint;


import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.SchemaCrawlerException;

public interface Linter
{

  void config(LinterConfig linterConfig);

  String getDescription();

  String getId();

  LintCollector getLintCollector();

  LintSeverity getSeverity();

  String getSummary();

  void lint(Database database)
    throws SchemaCrawlerException;

  void setLintCollector(LintCollector lintCollector);

  void setSeverity(LintSeverity severity);

}

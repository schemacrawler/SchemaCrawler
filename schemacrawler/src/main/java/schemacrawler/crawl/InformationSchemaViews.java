/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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
package schemacrawler.crawl;


import java.util.HashMap;
import java.util.Map;

import schemacrawler.main.BaseOptions;
import sf.util.Utilities;

/**
 * The database specific views to get additional database metadata in a
 * standard format.
 * 
 * @author Sualeh Fatehi
 */
public final class InformationSchemaViews
  extends BaseOptions
{

  private static final long serialVersionUID = 3587581365346059044L;

  private final Map<String, String> informationSchemaViewsSql;

  public InformationSchemaViews(final Map<String, String> informationSchemaViewsSql)
  {
    if (informationSchemaViewsSql != null)
    {
      this.informationSchemaViewsSql = new HashMap<String, String>(informationSchemaViewsSql);
    }
    else
    {
      this.informationSchemaViewsSql = new HashMap<String, String>();
    }
  }

  /**
   * Gets the table check constraints SQL from the additional
   * configuration.
   * 
   * @return Table check constraints SQL.
   */
  String getCheckConstraintsSql()
  {
    return informationSchemaViewsSql
      .get("select.INFORMATION_SCHEMA.CHECK_CONSTRAINTS");
  }

  /**
   * Gets the index info SQL from the additional configuration.
   * 
   * @return Index info constraints SQL.
   */
  String getIndexInfoSql()
  {
    return informationSchemaViewsSql.get("getIndexInfo");
  }

  /**
   * Gets the procedure definitions SQL from the additional
   * configuration.
   * 
   * @return Procedure defnitions SQL.
   */
  String getRoutinesSql()
  {
    return informationSchemaViewsSql.get("select.INFORMATION_SCHEMA.ROUTINES");
  }

  /**
   * Gets the table constraints SQL from the additional configuration.
   * 
   * @return Table constraints SQL.
   */
  String getTableConstraintsSql()
  {
    return informationSchemaViewsSql
      .get("select.INFORMATION_SCHEMA.TABLE_CONSTRAINTS");
  }

  /**
   * Gets the trigger definitions SQL from the additional configuration.
   * 
   * @return Trigger defnitions SQL.
   */
  String getTriggersSql()
  {
    return informationSchemaViewsSql.get("select.INFORMATION_SCHEMA.TRIGGERS");
  }

  /**
   * Gets the view definitions SQL from the additional configuration.
   * 
   * @return View defnitions SQL.
   */
  String getViewsSql()
  {
    return informationSchemaViewsSql.get("select.INFORMATION_SCHEMA.VIEWS");
  }

  boolean hasCheckConstraintsSql()
  {
    return !Utilities.isBlank(getCheckConstraintsSql());
  }

  boolean hasIndexInfoSql()
  {
    return !Utilities.isBlank(getIndexInfoSql());
  }

  boolean hasRoutinesSql()
  {
    return !Utilities.isBlank(getRoutinesSql());
  }

  boolean hasTableConstraintsSql()
  {
    return !Utilities.isBlank(getTableConstraintsSql());
  }

  boolean hasViewsSql()
  {
    return !Utilities.isBlank(getViewsSql());
  }

  @Override
  public String toString()
  {
    return informationSchemaViewsSql.toString();
  }
}

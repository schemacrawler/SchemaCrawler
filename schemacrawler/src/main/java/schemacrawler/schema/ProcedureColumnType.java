/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.schema;


import java.sql.DatabaseMetaData;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An enumeration wrapper around procedure column types.
 */
public enum ProcedureColumnType
{

  /** Unknown. */
  unknown(DatabaseMetaData.procedureColumnUnknown, "unknown"),
  /** In. */
  in(DatabaseMetaData.procedureColumnIn, "in"),
  /** In/ out. */
  inOut(DatabaseMetaData.procedureColumnInOut, "in/ out"),
  /** Out. */
  out(DatabaseMetaData.procedureColumnOut, "out"),
  /** Return. */
  returnValue(DatabaseMetaData.procedureColumnReturn, "return"),
  /** Return. */
  result(DatabaseMetaData.procedureColumnResult, "result");

  private static final Logger LOGGER = Logger
    .getLogger(ProcedureColumnType.class.getName());

  /**
   * Gets the enum value from the integer.
   * 
   * @param id
   *        Id of the integer
   * @return ForeignKeyDeferrability
   */
  public static ProcedureColumnType valueOf(final int id)
  {
    for (final ProcedureColumnType type: ProcedureColumnType.values())
    {
      if (type.getId() == id)
      {
        return type;
      }
    }
    LOGGER.log(Level.FINE, "Unknown id " + id);
    return unknown;
  }

  private final int id;
  private final String text;

  private ProcedureColumnType(final int id, final String text)
  {
    this.id = id;
    this.text = text;
  }

  /**
   * Gets the id.
   * 
   * @return id
   */
  public int getId()
  {
    return id;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  @Override
  public String toString()
  {
    return text;
  }
}

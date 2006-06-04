/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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


import java.sql.SQLException;

/**
 * SchemaRetriever uses database metadata to get the details about the schema.
 * 
 * @author sfatehi
 */
abstract class AbstractRetriever
{

  protected static final String ASC_OR_DESC = "ASC_OR_DESC";
  protected static final String CARDINALITY = "CARDINALITY";
  protected static final String COLUMN_DEFAULT = "COLUMN_DEF";
  protected static final String COLUMN_NAME = "COLUMN_NAME";
  protected static final String COLUMN_SIZE = "COLUMN_SIZE";
  protected static final String DATA_TYPE = "DATA_TYPE";
  protected static final String DECIMAL_DIGITS = "DECIMAL_DIGITS";
  protected static final String DEFERRABILITY = "DEFERRABILITY";
  protected static final String DELETE_RULE = "DELETE_RULE";
  protected static final String FKCOLUMN_NAME = "FKCOLUMN_NAME";
  protected static final String FKTABLE_NAME = "FKTABLE_NAME";
  protected static final String INDEX_NAME = "INDEX_NAME";
  protected static final String KEY_SEQ = "KEY_SEQ";
  protected static final String NON_UNIQUE = "NON_UNIQUE";
  protected static final String NULLABLE = "NULLABLE";
  protected static final String ORDINAL_POSITION = "ORDINAL_POSITION";
  protected static final String PAGES = "PAGES";
  protected static final String PKCOLUMN_NAME = "PKCOLUMN_NAME";
  protected static final String PKTABLE_NAME = "PKTABLE_NAME";
  protected static final String PK_NAME = "PK_NAME";
  protected static final String REMARKS = "REMARKS";
  protected static final String TABLE_NAME = "TABLE_NAME";
  protected static final String TABLE_SCHEMA = "TABLE_SCHEM";
  protected static final String TABLE_TYPE = "TABLE_TYPE";
  protected static final String TYPE = "TYPE";
  protected static final String TYPE_NAME = "TYPE_NAME";
  protected static final String UPDATE_RULE = "UPDATE_RULE";

  protected static final String IS_GRANTABLE = "IS_GRANTABLE";
  protected static final String GRANTEE = "GRANTEE";
  protected static final String GRANTOR = "GRANTOR";
  protected static final String PRIVILEGE = "PRIVILEGE";

  protected static final String PROCEDURE_NAME = "PROCEDURE_NAME";
  protected static final String PROCEDURE_SCHEMA = "PROCEDURE_SCHEM";
  protected static final String PROCEDURE_TYPE = "PROCEDURE_TYPE";

  protected static final String UNKNOWN = "<unknown>";
  protected static final int FETCHSIZE = 5;

  private final RetrieverConnection retrieverConnection;

  /**
   * Constructs a SchemaCrawler object, from a connection.
   * 
   * @param connection
   *          An open database connection.
   * @param driverClassName
   *          Class name of the JDBC driver
   * @param schemaPatternString
   *          JDBC schema pattern, or null
   * @throws SQLException
   *           On a SQL exception
   */
  AbstractRetriever(final RetrieverConnection retrieverConnection)
    throws SQLException
  {
    this.retrieverConnection = retrieverConnection;
  }

  protected RetrieverConnection getRetrieverConnection()
  {
    return retrieverConnection;
  }

}

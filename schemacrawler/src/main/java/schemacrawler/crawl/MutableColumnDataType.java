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

package schemacrawler.crawl;


import java.sql.Types;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.SearchableType;
import schemacrawler.schema.SqlDataType;

/**
 * Represents a column type. Provides the java.sql.Types type, the
 * java.sql.Types type name, and the database specific data type name.
 * 
 * @author Sualeh Fatehi
 */
final class MutableColumnDataType
  extends AbstractDatabaseObject
  implements ColumnDataType
{

  private static final long serialVersionUID = 3688503281676530744L;

  private boolean userDefined;
  private SqlDataType type;
  private long precision;
  private String literalPrefix;
  private String literalSuffix;
  private String createParameters;
  private boolean nullable;
  private boolean caseSensitive;
  private SearchableType searchable;
  private boolean unsigned;
  private boolean fixedPrecisionScale;
  private boolean autoIncrementable;
  private String localizedTypeName;
  private int minimumScale;
  private int maximumScale;
  private int numPrecisionRadix; // usually 2 or 10
  private ColumnDataType baseType;
  private String typeClassName;

  MutableColumnDataType(final String catalogName,
                        final String schemaName,
                        final String name)
  {
    super(catalogName, schemaName, name);
    // Default values
    searchable = SearchableType.unknown;
    type = SqlDataType.UNKNOWN;
    createParameters = "";
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#getBaseType()
   */
  public ColumnDataType getBaseType()
  {
    return baseType;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#getCreateParameters()
   */
  public String getCreateParameters()
  {
    return createParameters;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#getDatabaseSpecificTypeName()
   */
  public String getDatabaseSpecificTypeName()
  {
    return getName();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#getLiteralPrefix()
   */
  public String getLiteralPrefix()
  {
    return literalPrefix;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#getLiteralSuffix()
   */
  public String getLiteralSuffix()
  {
    return literalSuffix;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#getLocalTypeName()
   */
  public String getLocalTypeName()
  {
    return localizedTypeName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#getMaximumScale()
   */
  public int getMaximumScale()
  {
    return maximumScale;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#getMinimumScale()
   */
  public int getMinimumScale()
  {
    return minimumScale;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#getNumPrecisionRadix()
   */
  public int getNumPrecisionRadix()
  {
    return numPrecisionRadix;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#getPrecision()
   */
  public long getPrecision()
  {
    return precision;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#getSearchable()
   */
  public SearchableType getSearchable()
  {
    return searchable;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#getType()
   */
  public int getType()
  {
    return type.getType();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#getTypeClassName()
   */
  public String getTypeClassName()
  {
    return typeClassName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#getTypeName()
   */
  public String getTypeName()
  {
    return type.getTypeName();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#isAutoIncrementable()
   */
  public boolean isAutoIncrementable()
  {
    return autoIncrementable;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#isBinaryType()
   */
  public boolean isBinaryType()
  {
    final int type = getType();
    return type == Types.CLOB || type == Types.BLOB
           || type == Types.LONGVARBINARY || type == Types.OTHER;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#isCaseSensitive()
   */
  public boolean isCaseSensitive()
  {
    return caseSensitive;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#isCharacterType()
   */
  public boolean isCharacterType()
  {
    final int type = getType();
    return type == Types.CHAR || type == Types.LONGVARCHAR
           || type == Types.VARCHAR || type == Types.CLOB;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#isDateType()
   */
  public boolean isDateType()
  {
    final int type = getType();
    return type == Types.TIMESTAMP || type == Types.TIME || type == Types.DATE;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#isFixedPrecisionScale()
   */
  public boolean isFixedPrecisionScale()
  {
    return fixedPrecisionScale;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#isIntegralType()
   */
  public boolean isIntegralType()
  {
    final int type = getType();
    return type == Types.INTEGER || type == Types.BIGINT
           || type == Types.SMALLINT || type == Types.TINYINT;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#isNullable()
   */
  public boolean isNullable()
  {
    return nullable;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#isRealType()
   */
  public boolean isRealType()
  {
    final int type = getType();
    return type == Types.DECIMAL || type == Types.FLOAT || type == Types.REAL
           || type == Types.DOUBLE || type == Types.NUMERIC;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#isUnsigned()
   */
  public boolean isUnsigned()
  {
    return unsigned;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#isUserDefined()
   */
  public boolean isUserDefined()
  {
    return userDefined;
  }

  void setAutoIncrementable(final boolean autoIncrementable)
  {
    this.autoIncrementable = autoIncrementable;
  }

  void setBaseType(final ColumnDataType baseType)
  {
    this.baseType = baseType;
  }

  void setCaseSensitive(final boolean caseSensitive)
  {
    this.caseSensitive = caseSensitive;
  }

  void setCreateParameters(final String createParams)
  {
    createParameters = createParams;
  }

  void setFixedPrecisionScale(final boolean fixedPrecisionScale)
  {
    this.fixedPrecisionScale = fixedPrecisionScale;
  }

  void setLiteralPrefix(final String literalPrefix)
  {
    this.literalPrefix = literalPrefix;
  }

  void setLiteralSuffix(final String literalSuffix)
  {
    this.literalSuffix = literalSuffix;
  }

  void setLocalTypeName(final String localTypeName)
  {
    localizedTypeName = localTypeName;
  }

  void setMaximumScale(final int maximumScale)
  {
    this.maximumScale = maximumScale;
  }

  void setMinimumScale(final int minimumScale)
  {
    this.minimumScale = minimumScale;
  }

  void setNullable(final boolean nullable)
  {
    this.nullable = nullable;
  }

  void setNumPrecisionRadix(final int numPrecisionRadix)
  {
    this.numPrecisionRadix = numPrecisionRadix;
  }

  void setPrecision(final long precision)
  {
    this.precision = precision;
  }

  void setSearchable(final int searchable)
  {
    this.searchable = SearchableType.valueOf(searchable);
  }

  void setType(final int type)
  {
    this.type = SqlDataType.lookupSqlDataType(type);
  }

  void setTypeClassName(final String typeClassName)
  {
    this.typeClassName = typeClassName;
  }

  void setUnsigned(final boolean unsignedAttribute)
  {
    unsigned = unsignedAttribute;
  }

  void setUserDefined(final boolean userDefined)
  {
    this.userDefined = userDefined;
  }

}

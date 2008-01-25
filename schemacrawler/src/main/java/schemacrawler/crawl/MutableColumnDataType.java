/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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


import java.sql.Types;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.SearchableType;
import schemacrawler.schema.SqlDataType;

/**
 * A column type. Provide the java.sql.Types type, the java.sql.Types
 * type name, and the database specific data type name.
 */
final class MutableColumnDataType
  extends AbstractNamedObject
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

  MutableColumnDataType(final String name)
  {
    super(name);
    // Default values
    searchable = SearchableType.unknown;
    type = SqlDataType.UNKNOWN;
    createParameters = "";
  }

  /**
   * {@inheritDoc}
   */
  public ColumnDataType getBaseType()
  {
    return baseType;
  }

  /**
   * Parameters used in type creation.
   * 
   * @return Parameters used in type creation.
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
   * @return Returns the literalPrefix.
   */
  public String getLiteralPrefix()
  {
    return literalPrefix;
  }

  /**
   * @return Returns the literalSuffix.
   */
  public String getLiteralSuffix()
  {
    return literalSuffix;
  }

  /**
   * @return Returns the localTypeName.
   */
  public String getLocalTypeName()
  {
    return localizedTypeName;
  }

  /**
   * @return Returns the maximumScale.
   */
  public int getMaximumScale()
  {
    return maximumScale;
  }

  /**
   * @return Returns the minimumScale.
   */
  public int getMinimumScale()
  {
    return minimumScale;
  }

  /**
   * @return Returns the numPrecisionRadix.
   */
  public int getNumPrecisionRadix()
  {
    return numPrecisionRadix;
  }

  /**
   * @return Returns the precision.
   */
  public long getPrecision()
  {
    return precision;
  }

  /**
   * @return Returns the searchable.
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
   * @return Returns the autoIncremented.
   */
  public boolean isAutoIncrementable()
  {
    return autoIncrementable;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isBinaryType()
  {
    final int type = getType();
    return type == Types.CLOB || type == Types.BLOB
           || type == Types.LONGVARBINARY || type == Types.OTHER;
  }

  /**
   * @return Returns the caseSensitive.
   */
  public boolean isCaseSensitive()
  {
    return caseSensitive;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isCharacterType()
  {
    final int type = getType();
    return type == Types.CHAR || type == Types.LONGVARCHAR
           || type == Types.VARCHAR || type == Types.CLOB;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isDateType()
  {
    final int type = getType();
    return type == Types.TIMESTAMP || type == Types.TIME || type == Types.DATE;
  }

  /**
   * @return Returns the fixedPrecisionScale.
   */
  public boolean isFixedPrecisionScale()
  {
    return fixedPrecisionScale;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isIntegralType()
  {
    final int type = getType();
    return type == Types.INTEGER || type == Types.BIGINT
           || type == Types.SMALLINT || type == Types.TINYINT;
  }

  /**
   * @return Returns the nullable.
   */
  public boolean isNullable()
  {
    return nullable;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isRealType()
  {
    final int type = getType();
    return type == Types.DECIMAL || type == Types.FLOAT || type == Types.REAL
           || type == Types.DOUBLE || type == Types.NUMERIC;
  }

  /**
   * @return Returns the unsignedAttribute.
   */
  public boolean isUnsigned()
  {
    return unsigned;
  }

  /**
   * @return Returns the userDefined.
   */
  public boolean isUserDefined()
  {
    return userDefined;
  }

  /**
   * @param autoIncremented
   *        The autoIncremented to set.
   */
  void setAutoIncrementable(final boolean autoIncrementable)
  {
    this.autoIncrementable = autoIncrementable;
  }

  void setBaseType(final ColumnDataType baseType)
  {
    this.baseType = baseType;
  }

  /**
   * @param caseSensitive
   *        The caseSensitive to set.
   */
  void setCaseSensitive(final boolean caseSensitive)
  {
    this.caseSensitive = caseSensitive;
  }

  /**
   * @param createParams
   *        The createParams to set.
   */
  void setCreateParameters(final String createParams)
  {
    createParameters = createParams;
  }

  /**
   * @param fixedPrecisionScale
   *        The fixedPrecisionScale to set.
   */
  void setFixedPrecisionScale(final boolean fixedPrecisionScale)
  {
    this.fixedPrecisionScale = fixedPrecisionScale;
  }

  /**
   * @param literalPrefix
   *        The literalPrefix to set.
   */
  void setLiteralPrefix(final String literalPrefix)
  {
    this.literalPrefix = literalPrefix;
  }

  /**
   * @param literalSuffix
   *        The literalSuffix to set.
   */
  void setLiteralSuffix(final String literalSuffix)
  {
    this.literalSuffix = literalSuffix;
  }

  /**
   * @param localTypeName
   *        The localTypeName to set.
   */
  void setLocalTypeName(final String localTypeName)
  {
    localizedTypeName = localTypeName;
  }

  /**
   * @param maximumScale
   *        The maximumScale to set.
   */
  void setMaximumScale(final int maximumScale)
  {
    this.maximumScale = maximumScale;
  }

  /**
   * @param minimumScale
   *        The minimumScale to set.
   */
  void setMinimumScale(final int minimumScale)
  {
    this.minimumScale = minimumScale;
  }

  /**
   * @param nullable
   *        The nullable to set.
   */
  void setNullable(final boolean nullable)
  {
    this.nullable = nullable;
  }

  /**
   * @param numPrecisionRadix
   *        The numPrecisionRadix to set.
   */
  void setNumPrecisionRadix(final int numPrecisionRadix)
  {
    this.numPrecisionRadix = numPrecisionRadix;
  }

  /**
   * @param precision
   *        The precision to set.
   */
  void setPrecision(final long precision)
  {
    this.precision = precision;
  }

  /**
   * @param searchable
   *        The searchable to set.
   */
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

  /**
   * @param unsignedAttribute
   *        The unsignedAttribute to set.
   */
  void setUnsigned(final boolean unsignedAttribute)
  {
    unsigned = unsignedAttribute;
  }

  /**
   * @param userDefined
   *        The userDefined to set.
   */
  void setUserDefined(final boolean userDefined)
  {
    this.userDefined = userDefined;
  }

}

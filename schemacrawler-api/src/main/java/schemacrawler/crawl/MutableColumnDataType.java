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

package schemacrawler.crawl;


import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SearchableType;
import sf.util.Utility;

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

  // Fields related to the java.sql.Types type
  private int javaSqlType;
  private String javaSqlTypeName;
  private String javaSqlTypeMappedClassName;

  // Other fields
  private boolean userDefined;
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

  MutableColumnDataType(final Schema schema, final String name)
  {
    super(schema, name);
    // Default values
    searchable = SearchableType.unknown;
    setTypeFromJavaSqlType(JavaSqlType.UNKNOWN);
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
    return javaSqlType;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#getTypeClassName()
   */
  public String getTypeClassName()
  {
    return javaSqlTypeMappedClassName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ColumnDataType#getTypeName()
   */
  public String getTypeName()
  {
    return javaSqlTypeName;
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
   * @see schemacrawler.schema.ColumnDataType#isCaseSensitive()
   */
  public boolean isCaseSensitive()
  {
    return caseSensitive;
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
   * @see schemacrawler.schema.ColumnDataType#isNullable()
   */
  public boolean isNullable()
  {
    return nullable;
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

  private void setTypeFromJavaSqlType(final JavaSqlType javaSqlType)
  {
    if (javaSqlType != null)
    {
      this.javaSqlType = javaSqlType.getJavaSqlType();
      javaSqlTypeName = javaSqlType.getJavaSqlTypeName();
      javaSqlTypeMappedClassName = javaSqlType.getJavaSqlTypeMappedClassName();
    }
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

  void setType(final int type, final String typeClassName)
  {
    setTypeFromJavaSqlType(JavaSqlTypesUtility.lookupSqlDataType(type));
    if (javaSqlTypeMappedClassName == null && !Utility.isBlank(typeClassName))
    {
      javaSqlTypeMappedClassName = typeClassName;
    }
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

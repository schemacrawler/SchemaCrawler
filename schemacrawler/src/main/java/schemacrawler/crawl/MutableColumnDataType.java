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


import java.lang.reflect.Field;
import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.SearchableType;

/**
 * A column type. Provide the java.sql.Types type, the java.sql.Types
 * type name, and the database specific data type name.
 */
final class MutableColumnDataType
  extends AbstractNamedObject
  implements ColumnDataType
{

  private static final long serialVersionUID = 3688503281676530744L;

  private static final Map<Integer, String> JAVA_SQL_TYPES = getJavaSqlTypes();

  private static Map<Integer, String> getJavaSqlTypes()
  {

    final Map<Integer, String> javaSqlTypes = new HashMap<Integer, String>();
    final Field[] staticFields = Types.class.getFields();
    for (final Field field: staticFields)
    {
      try
      {
        final String fieldName = field.getName();
        final Integer fieldValue = (Integer) field.get(null);
        javaSqlTypes.put(fieldValue, fieldName);
      }
      catch (final SecurityException e)
      {
        continue;
      }
      catch (final IllegalAccessException e)
      {
        continue;
      }
    }

    return Collections.unmodifiableMap(javaSqlTypes);
  }

  private boolean userDefined;
  private int type;
  private long precision; // maximum precision, long to account for the
  // Oracle driver
  private String literalPrefix; // prefix used to quote a literal
  private String literalSuffix; // suffix used to quote a literal
  private String createParameters; // parameters used in creating the
  // type
  private boolean nullable; // can you use null for this type
  private boolean caseSensitive; // is it case sensitive
  private SearchableType searchable; // can you use "where" based on
  // this type
  private boolean unsigned; // is it unsigned
  private boolean fixedPrecisionScale; // can it be a money value
  private boolean autoIncrementable; // can it be used for
  // auto-increment
  private String localTypeName; // localized version of type name (may
  // be null)
  private int minimumScale; // minimum scale supported
  private int maximumScale; // maximum scale supported
  private int numPrecisionRadix; // usually 2 or 10
  private ColumnDataType baseType;
  private String typeClassName;

  MutableColumnDataType(final String name)
  {
    super(name);
  }

  /**
   * {@inheritDoc}
   */
  public ColumnDataType getBaseType()
  {
    return baseType;
  }

  /**
   * @return Returns the createParams.
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
    return localTypeName;
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
    return type;
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
    String typeName = JAVA_SQL_TYPES.get(new Integer(type));
    if (typeName == null)
    {
      typeName = "<UNKNOWN>";
    }
    return typeName;
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
    return type == Types.CHAR || type == Types.LONGVARCHAR
           || type == Types.VARCHAR || type == Types.CLOB;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isDateType()
  {
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
    this.localTypeName = localTypeName;
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
    this.type = type;
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

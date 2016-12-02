/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.crawl;


import static sf.util.Utility.isBlank;

import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.JavaSqlType;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SearchableType;
import sf.util.StringFormat;

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

  private static final Logger LOGGER = Logger
    .getLogger(SchemaCrawler.class.getName());

  private JavaSqlType javaSqlType;

  private Class<?> javaSqlTypeMappedClass;
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

    javaSqlType = JavaSqlType.UNKNOWN;
    javaSqlTypeMappedClass = Object.class;
    searchable = SearchableType.unknown;
    createParameters = "";
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.ColumnDataType#getBaseType()
   */
  @Override
  public ColumnDataType getBaseType()
  {
    return baseType;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.ColumnDataType#getCreateParameters()
   */
  @Override
  public String getCreateParameters()
  {
    return createParameters;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.ColumnDataType#getDatabaseSpecificTypeName()
   */
  @Override
  public String getDatabaseSpecificTypeName()
  {
    return getName();
  }

  @Override
  public JavaSqlType getJavaSqlType()
  {
    return javaSqlType;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.ColumnDataType#getLiteralPrefix()
   */
  @Override
  public String getLiteralPrefix()
  {
    return literalPrefix;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.ColumnDataType#getLiteralSuffix()
   */
  @Override
  public String getLiteralSuffix()
  {
    return literalSuffix;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.ColumnDataType#getLocalTypeName()
   */
  @Override
  public String getLocalTypeName()
  {
    return localizedTypeName;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.ColumnDataType#getMaximumScale()
   */
  @Override
  public int getMaximumScale()
  {
    return maximumScale;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.ColumnDataType#getMinimumScale()
   */
  @Override
  public int getMinimumScale()
  {
    return minimumScale;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.ColumnDataType#getNumPrecisionRadix()
   */
  @Override
  public int getNumPrecisionRadix()
  {
    return numPrecisionRadix;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.ColumnDataType#getPrecision()
   */
  @Override
  public long getPrecision()
  {
    return precision;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.ColumnDataType#getSearchable()
   */
  @Override
  public SearchableType getSearchable()
  {
    return searchable;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.ColumnDataType#getTypeMappedClass()
   */
  @Override
  public Class<?> getTypeMappedClass()
  {
    return javaSqlTypeMappedClass;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.ColumnDataType#isAutoIncrementable()
   */
  @Override
  public boolean isAutoIncrementable()
  {
    return autoIncrementable;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.ColumnDataType#isCaseSensitive()
   */
  @Override
  public boolean isCaseSensitive()
  {
    return caseSensitive;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.ColumnDataType#isFixedPrecisionScale()
   */
  @Override
  public boolean isFixedPrecisionScale()
  {
    return fixedPrecisionScale;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.ColumnDataType#isNullable()
   */
  @Override
  public boolean isNullable()
  {
    return nullable;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.ColumnDataType#isUnsigned()
   */
  @Override
  public boolean isUnsigned()
  {
    return unsigned;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.ColumnDataType#isUserDefined()
   */
  @Override
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

  void setJavaSqlType(final JavaSqlType javaSqlType)
  {
    if (javaSqlType != null)
    {
      this.javaSqlType = javaSqlType;
    }
    else
    {
      this.javaSqlType = JavaSqlType.UNKNOWN;
    }
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

  void setSearchable(final SearchableType searchable)
  {
    this.searchable = searchable;
  }

  void setTypeMappedClass(final Class<?> mappedClass)
  {
    if (mappedClass != null)
    {
      javaSqlTypeMappedClass = mappedClass;
    }
    else
    {
      javaSqlTypeMappedClass = Object.class;
    }
  }

  void setTypeMappedClass(final String mappedClassName)
  {
    if (!isBlank(mappedClassName))
    {
      try
      {
        javaSqlTypeMappedClass = Class.forName(mappedClassName);
      }
      catch (final ClassNotFoundException e)
      {
        LOGGER.log(Level.FINE,
                   e,
                   new StringFormat("Could not load mapped class, %s",
                                    mappedClassName));
        javaSqlTypeMappedClass = Object.class;
      }
    }
    else
    {
      javaSqlTypeMappedClass = Object.class;
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

  @Override
  public String getLookupKey() {
    // Change the lookup key so that it includes the java sql type. This allows for the same database specific type name to be mapped to multiple
    // ColumnDataTypes. This is required for Oracle NUMBER, which can be mapped to multiple types, ranging from BIT/Boolean to BigDecimal
    return super.getLookupKey() + "." + this.getJavaSqlType().getJavaSqlType();
  }
}

/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.JavaSqlType;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SearchableType;
import schemacrawler.utility.Identifiers;
import sf.util.SchemaCrawlerLogger;
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

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(SchemaCrawler.class.getName());

  private static final long serialVersionUID = 3688503281676530744L;

  private boolean autoIncrementable;
  private ColumnDataType baseType;
  private boolean caseSensitive;
  private String createParameters;
  private boolean fixedPrecisionScale;
  private transient String fullName;
  private JavaSqlType javaSqlType;
  private Class<?> javaSqlTypeMappedClass;
  private String literalPrefix;
  private String literalSuffix;
  private String localizedTypeName;
  private int maximumScale;
  private int minimumScale;
  private boolean nullable;
  private int numPrecisionRadix; // usually 2 or 10
  private long precision;
  private SearchableType searchable;
  private boolean unsigned;
  private boolean userDefined;
  private List<String> enumValues;

  MutableColumnDataType(final ColumnDataType columnDataType)
  {
    this(columnDataType.getSchema(), columnDataType.getName());

    autoIncrementable = columnDataType.isAutoIncrementable();
    baseType = columnDataType.getBaseType();
    caseSensitive = columnDataType.isCaseSensitive();
    createParameters = columnDataType.getCreateParameters();
    fixedPrecisionScale = columnDataType.isFixedPrecisionScale();
    javaSqlType = columnDataType.getJavaSqlType();
    javaSqlTypeMappedClass = columnDataType.getTypeMappedClass();
    literalPrefix = columnDataType.getLiteralPrefix();
    literalSuffix = columnDataType.getLiteralSuffix();
    localizedTypeName = columnDataType.getLocalTypeName();
    maximumScale = columnDataType.getMaximumScale();
    minimumScale = columnDataType.getMinimumScale();
    nullable = columnDataType.isNullable();
    numPrecisionRadix = columnDataType.getNumPrecisionRadix();
    precision = columnDataType.getPrecision();
    searchable = columnDataType.getSearchable();
    unsigned = columnDataType.isUnsigned();
    userDefined = columnDataType.isUserDefined();
    enumValues = new ArrayList<>(columnDataType.getEnumValues());
  }

  MutableColumnDataType(final Schema schema, final String name)
  {
    super(schema, name);

    javaSqlType = JavaSqlType.UNKNOWN;
    javaSqlTypeMappedClass = Object.class;
    searchable = SearchableType.unknown;

    literalPrefix = "";
    literalSuffix = "";
    createParameters = "";
    localizedTypeName = "";

    enumValues = new ArrayList<>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ColumnDataType getBaseType()
  {
    return baseType;
  }

  void setBaseType(final ColumnDataType baseType)
  {
    this.baseType = baseType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreateParameters()
  {
    return createParameters;
  }

  void setCreateParameters(final String createParams)
  {
    createParameters = createParams;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDatabaseSpecificTypeName()
  {
    return getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getEnumValues()
  {
    return enumValues;
  }

  void setEnumValues(final List<String> enumValues)
  {
    if (enumValues == null)
    {
      this.enumValues = new ArrayList<>();
    }
    else
    {
      this.enumValues = enumValues;
    }
  }

  @Override
  public boolean isEnumerated()
  {
    return !enumValues.isEmpty();
  }

  @Override
  public JavaSqlType getJavaSqlType()
  {
    return javaSqlType;
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

  /**
   * {@inheritDoc}
   */
  @Override
  public String getLiteralPrefix()
  {
    return literalPrefix;
  }

  void setLiteralPrefix(final String literalPrefix)
  {
    this.literalPrefix = literalPrefix;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getLiteralSuffix()
  {
    return literalSuffix;
  }

  void setLiteralSuffix(final String literalSuffix)
  {
    this.literalSuffix = literalSuffix;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getLocalTypeName()
  {
    return localizedTypeName;
  }

  void setLocalTypeName(final String localTypeName)
  {
    localizedTypeName = localTypeName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getMaximumScale()
  {
    return maximumScale;
  }

  void setMaximumScale(final int maximumScale)
  {
    this.maximumScale = maximumScale;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getMinimumScale()
  {
    return minimumScale;
  }

  void setMinimumScale(final int minimumScale)
  {
    this.minimumScale = minimumScale;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getNumPrecisionRadix()
  {
    return numPrecisionRadix;
  }

  void setNumPrecisionRadix(final int numPrecisionRadix)
  {
    this.numPrecisionRadix = numPrecisionRadix;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long getPrecision()
  {
    return precision;
  }

  void setPrecision(final long precision)
  {
    this.precision = precision;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SearchableType getSearchable()
  {
    return searchable;
  }

  void setSearchable(final SearchableType searchable)
  {
    this.searchable = searchable;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<?> getTypeMappedClass()
  {
    return javaSqlTypeMappedClass;
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
                   new StringFormat("Could not load mapped class <%s>",
                                    mappedClassName),
                   e);
        javaSqlTypeMappedClass = Object.class;
      }
    }
    else
    {
      javaSqlTypeMappedClass = Object.class;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isAutoIncrementable()
  {
    return autoIncrementable;
  }

  void setAutoIncrementable(final boolean autoIncrementable)
  {
    this.autoIncrementable = autoIncrementable;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isCaseSensitive()
  {
    return caseSensitive;
  }

  void setCaseSensitive(final boolean caseSensitive)
  {
    this.caseSensitive = caseSensitive;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isFixedPrecisionScale()
  {
    return fixedPrecisionScale;
  }

  void setFixedPrecisionScale(final boolean fixedPrecisionScale)
  {
    this.fixedPrecisionScale = fixedPrecisionScale;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isNullable()
  {
    return nullable;
  }

  void setNullable(final boolean nullable)
  {
    this.nullable = nullable;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isUnsigned()
  {
    return unsigned;
  }

  void setUnsigned(final boolean unsignedAttribute)
  {
    unsigned = unsignedAttribute;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isUserDefined()
  {
    return userDefined;
  }

  void setUserDefined(final boolean userDefined)
  {
    this.userDefined = userDefined;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getFullName()
  {
    buildFullName();
    return fullName;
  }

  private void buildFullName()
  {
    if (fullName != null)
    {
      return;
    }
    final Schema schema = getSchema();
    if (!isBlank(schema.getFullName()))
    {
      final Identifiers identifiers = Identifiers
        .identifiers()
        .withIdentifierQuoteString("\"")
        .build();
      fullName = identifiers.quoteFullName(this);
    }
    else
    {
      // System data-types are reserved words, but should not be quoted
      fullName = getName();
    }
  }

}

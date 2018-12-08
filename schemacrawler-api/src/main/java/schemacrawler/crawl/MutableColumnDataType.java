/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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

  private static final long serialVersionUID = 3688503281676530744L;

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(SchemaCrawler.class.getName());

  private transient String fullName;
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

    literalPrefix = "";
    literalSuffix = "";
    createParameters = "";
    localizedTypeName = "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ColumnDataType getBaseType()
  {
    return baseType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreateParameters()
  {
    return createParameters;
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
  public String getFullName()
  {
    buildFullName();
    return fullName;
  }

  @Override
  public JavaSqlType getJavaSqlType()
  {
    return javaSqlType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getLiteralPrefix()
  {
    return literalPrefix;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getLiteralSuffix()
  {
    return literalSuffix;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getLocalTypeName()
  {
    return localizedTypeName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getMaximumScale()
  {
    return maximumScale;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getMinimumScale()
  {
    return minimumScale;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getNumPrecisionRadix()
  {
    return numPrecisionRadix;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long getPrecision()
  {
    return precision;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SearchableType getSearchable()
  {
    return searchable;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<?> getTypeMappedClass()
  {
    return javaSqlTypeMappedClass;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isAutoIncrementable()
  {
    return autoIncrementable;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isCaseSensitive()
  {
    return caseSensitive;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isFixedPrecisionScale()
  {
    return fixedPrecisionScale;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isNullable()
  {
    return nullable;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isUnsigned()
  {
    return unsigned;
  }

  /**
   * {@inheritDoc}
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

  void setUnsigned(final boolean unsignedAttribute)
  {
    unsigned = unsignedAttribute;
  }

  void setUserDefined(final boolean userDefined)
  {
    this.userDefined = userDefined;
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
      final Identifiers identifiers = Identifiers.identifiers()
        .withIdentifierQuoteString("\"").build();
      fullName = identifiers.quoteFullName(this);
    }
    else
    {
      // System data-types are reserved words, but should not be quoted
      fullName = getName();
    }
  }

}

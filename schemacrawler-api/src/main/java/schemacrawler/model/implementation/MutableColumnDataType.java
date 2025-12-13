/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.model.implementation;


import schemacrawler.crawl.SchemaCrawler;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.DataTypeType;
import schemacrawler.schema.JavaSqlType;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SearchableType;
import schemacrawler.schema.Identifiers;
import us.fatehi.utility.string.StringFormat;

/**
 * Represents a column type. Provides the java.sql.Types type, the java.sql.Types type name, and the
 * database specific data type name.
 */
public final class MutableColumnDataType extends AbstractDatabaseObject implements ColumnDataType {

  @Serial private static final long serialVersionUID = 3688503281676530744L;

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawler.class.getName());

  private final DataTypeType type;
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
  private List<String> enumValues;

  public MutableColumnDataType(final ColumnDataType columnDataType) {
    this(columnDataType.getSchema(), columnDataType.getName(), columnDataType.getType());

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
    enumValues = new ArrayList<>(columnDataType.getEnumValues());
  }

  public MutableColumnDataType(final Schema schema, final String name, final DataTypeType type) {
    super(schema, name);

    requireNonNull(type, "No type provided");
    this.type = type;

    javaSqlType = JavaSqlType.UNKNOWN;
    javaSqlTypeMappedClass = Object.class;
    searchable = SearchableType.unknown;

    literalPrefix = "";
    literalSuffix = "";
    createParameters = "";
    localizedTypeName = "";

    enumValues = new ArrayList<>();
  }

  /** {@inheritDoc} */
  @Override
  public ColumnDataType getBaseType() {
    return baseType;
  }

  /** {@inheritDoc} */
  @Override
  public String getCreateParameters() {
    return createParameters;
  }

  /** {@inheritDoc} */
  @Override
  public List<String> getEnumValues() {
    return new ArrayList<>(enumValues);
  }

  /** {@inheritDoc} */
  @Override
  public String getFullName() {
    buildFullName();
    return fullName;
  }

  @Override
  public JavaSqlType getJavaSqlType() {
    return javaSqlType;
  }

  /** {@inheritDoc} */
  @Override
  public String getLiteralPrefix() {
    return literalPrefix;
  }

  /** {@inheritDoc} */
  @Override
  public String getLiteralSuffix() {
    return literalSuffix;
  }

  /** {@inheritDoc} */
  @Override
  public String getLocalTypeName() {
    return localizedTypeName;
  }

  /** {@inheritDoc} */
  @Override
  public int getMaximumScale() {
    return maximumScale;
  }

  /** {@inheritDoc} */
  @Override
  public int getMinimumScale() {
    return minimumScale;
  }

  /** {@inheritDoc} */
  @Override
  public int getNumPrecisionRadix() {
    return numPrecisionRadix;
  }

  /** {@inheritDoc} */
  @Override
  public long getPrecision() {
    return precision;
  }

  /** {@inheritDoc} */
  @Override
  public SearchableType getSearchable() {
    return searchable;
  }

  @Override
  public DataTypeType getType() {
    return type;
  }

  /** {@inheritDoc} */
  @Override
  public Class<?> getTypeMappedClass() {
    return javaSqlTypeMappedClass;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAutoIncrementable() {
    return autoIncrementable;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isCaseSensitive() {
    return caseSensitive;
  }

  @Override
  public boolean isEnumerated() {
    return !enumValues.isEmpty();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isFixedPrecisionScale() {
    return fixedPrecisionScale;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNullable() {
    return nullable;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isUnsigned() {
    return unsigned;
  }

  @Override
  public void withQuoting(final Identifiers identifiers) {
    if (identifiers == null) {
      return;
    }
    buildFullName(identifiers);
  }

  public void setAutoIncrementable(final boolean autoIncrementable) {
    this.autoIncrementable = autoIncrementable;
  }

  public void setBaseType(final ColumnDataType baseType) {
    this.baseType = baseType;
  }

  public void setCaseSensitive(final boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
  }

  public void setCreateParameters(final String createParams) {
    createParameters = createParams;
  }

  public void setEnumValues(final List<String> enumValues) {
    if (enumValues == null) {
      this.enumValues = new ArrayList<>();
    } else {
      this.enumValues = new ArrayList<>(enumValues);
    }
  }

  public void setFixedPrecisionScale(final boolean fixedPrecisionScale) {
    this.fixedPrecisionScale = fixedPrecisionScale;
  }

  public void setJavaSqlType(final JavaSqlType javaSqlType) {
    if (javaSqlType != null) {
      this.javaSqlType = javaSqlType;
    } else {
      this.javaSqlType = JavaSqlType.UNKNOWN;
    }
  }

  public void setLiteralPrefix(final String literalPrefix) {
    this.literalPrefix = literalPrefix;
  }

  public void setLiteralSuffix(final String literalSuffix) {
    this.literalSuffix = literalSuffix;
  }

  public void setLocalTypeName(final String localTypeName) {
    localizedTypeName = localTypeName;
  }

  public void setMaximumScale(final int maximumScale) {
    this.maximumScale = maximumScale;
  }

  public void setMinimumScale(final int minimumScale) {
    this.minimumScale = minimumScale;
  }

  public void setNullable(final boolean nullable) {
    this.nullable = nullable;
  }

  public void setNumPrecisionRadix(final int numPrecisionRadix) {
    this.numPrecisionRadix = numPrecisionRadix;
  }

  public void setPrecision(final long precision) {
    this.precision = precision;
  }

  public void setSearchable(final SearchableType searchable) {
    this.searchable = searchable;
  }

  public void setTypeMappedClass(final Class<?> mappedClass) {
    if (mappedClass != null) {
      javaSqlTypeMappedClass = mappedClass;
    } else {
      javaSqlTypeMappedClass = Object.class;
    }
  }

  public void setTypeMappedClass(final String mappedClassName) {
    if (!isBlank(mappedClassName)) {
      try {
        javaSqlTypeMappedClass = Class.forName(mappedClassName);
      } catch (final ClassNotFoundException e) {
        LOGGER.log(
            Level.FINE, e, new StringFormat("Could not load mapped class <%s>", mappedClassName));
        javaSqlTypeMappedClass = Object.class;
      }
    } else {
      javaSqlTypeMappedClass = Object.class;
    }
  }

  public void setUnsigned(final boolean unsignedAttribute) {
    unsigned = unsignedAttribute;
  }

  private void buildFullName() {
    buildFullName(Identifiers.STANDARD);
  }

  private void buildFullName(final Identifiers identifiers) {
    if (identifiers == null || fullName != null) {
      return;
    }
    final Schema schema = getSchema();
    if (!isBlank(schema.getFullName())) {
      fullName = identifiers.quoteFullName(this);
    } else {
      // System data-types are reserved words, but should not be quoted
      fullName = getName();
    }
  }
}

/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugin;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import schemacrawler.plugin.EnumDataTypeInfo.EnumDataTypeTypes;
import schemacrawler.schema.TypedObject;

public class EnumDataTypeInfo implements TypedObject<EnumDataTypeTypes> {

  public enum EnumDataTypeTypes {
    not_enumerated,
    enumerated_data_type,
    enumerated_column;
  }

  public static final EnumDataTypeInfo EMPTY_ENUM_DATA_TYPE_INFO =
      new EnumDataTypeInfo(EnumDataTypeTypes.not_enumerated, emptyList());

  private final EnumDataTypeTypes type;
  private final List<String> enumValues;

  public EnumDataTypeInfo(final EnumDataTypeTypes type, final List<String> enumValues) {
    this.type = requireNonNull(type, "No enumeration data-type type provided");
    this.enumValues = new ArrayList<>(requireNonNull(enumValues, "No enum values list provided"));
  }

  public List<String> getEnumValues() {
    return new ArrayList<>(enumValues);
  }

  @Override
  public EnumDataTypeTypes getType() {
    return type;
  }

  @Override
  public String toString() {
    return "EnumDataTypeInfo [" + type + ", " + enumValues + "]";
  }
}

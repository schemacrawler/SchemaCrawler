package schemacrawler.plugin;
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

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class EnumDataTypeInfo
{

  public static EnumDataTypeInfo EMPTY_ENUM_DATA_TYPE_INFO =
    new EnumDataTypeInfo(false, false, new ArrayList<>());

  private final boolean isColumnEnumerated;
  private final boolean isColumnDataTypeEnumerated;
  private final List<String> enumValues;

  public EnumDataTypeInfo(final boolean isColumnEnumerated,
                          final boolean isColumnDataTypeEnumerated,
                          final List<String> enumValues)
  {
    this.isColumnEnumerated = isColumnEnumerated;
    this.isColumnDataTypeEnumerated = isColumnDataTypeEnumerated;
    this.enumValues = new ArrayList<>(requireNonNull(enumValues,
                                                     "No enum values list provided"));
  }

  public boolean isColumnEnumerated()
  {
    return isColumnEnumerated;
  }

  public boolean isColumnDataTypeEnumerated()
  {
    return isColumnDataTypeEnumerated;
  }

  public List<String> getEnumValues()
  {
    return enumValues;
  }

  @Override
  public String toString()
  {
    return new StringJoiner(", ",
                            EnumDataTypeInfo.class.getSimpleName() + "[",
                            "]")
      .add("isColumnEnumerated=" + isColumnEnumerated)
      .add("isColumnDataTypeEnumerated=" + isColumnDataTypeEnumerated)
      .add("enumValues=" + enumValues)
      .toString();
  }

}

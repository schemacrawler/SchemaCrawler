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


import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaReference;

import java.util.Optional;

class ColumnDataTypes
  extends NamedObjectList<MutableColumnDataType>
{

  private static final long serialVersionUID = 6793135093651666453L;

  MutableColumnDataType lookupColumnDataTypeByType(final int type)
  {
    final SchemaReference systemSchema = new SchemaReference();
    MutableColumnDataType columnDataType = null;
    for (final MutableColumnDataType currentColumnDataType: this)
    {
      if (type == currentColumnDataType.getJavaSqlType().getJavaSqlType())
      {
        columnDataType = currentColumnDataType;
        if (columnDataType.getSchema().equals(systemSchema))
        {
          break;
        }
      }
    }
    return columnDataType;
  }

  Optional<MutableColumnDataType> lookup(final NamedObject namedObject, final String name, final int sqlType) {
    return this.lookup(namedObject, name + "." + sqlType);
  }

}

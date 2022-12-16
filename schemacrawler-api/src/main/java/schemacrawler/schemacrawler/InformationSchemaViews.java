/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.schemacrawler;

import static java.util.Objects.requireNonNull;

import java.util.EnumMap;
import java.util.Map;

import us.fatehi.utility.ObjectToString;

/** The database specific views to get additional database metadata in a standard format. */
public final class InformationSchemaViews implements Options {

  private final Map<InformationSchemaKey, String> informationSchemaQueries;

  /** Creates empty information schema views. */
  InformationSchemaViews() {
    this(null);
  }

  /**
   * Information schema views from a map.
   *
   * @param informationSchemaViewsQueries Map of information schema view definitions.
   */
  InformationSchemaViews(final Map<InformationSchemaKey, String> informationSchemaViewsQueries) {
    informationSchemaQueries = new EnumMap<>(InformationSchemaKey.class);
    if (informationSchemaViewsQueries != null) {
      informationSchemaQueries.putAll(informationSchemaViewsQueries);
    }
  }

  /**
   * Gets the additional attributes SQL for columns, from the additional configuration.
   *
   * @return Additional attributes SQL for columns.
   */
  public Query getQuery(final InformationSchemaKey key) {
    requireNonNull(key, "No SQL query key provided");
    return new Query(key.description(), informationSchemaQueries.get(key));
  }

  public boolean hasQuery(final InformationSchemaKey key) {
    requireNonNull(key, "No SQL query key provided");
    return informationSchemaQueries.containsKey(key);
  }

  public boolean isEmpty() {
    return informationSchemaQueries.isEmpty();
  }

  public int size() {
    return informationSchemaQueries.size();
  }

  @Override
  public String toString() {
    return ObjectToString.toString(informationSchemaQueries);
  }

  protected Map<InformationSchemaKey, String> getAllInformationSchemaViews() {
    return new EnumMap<>(informationSchemaQueries);
  }
}

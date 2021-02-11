/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.loader.attributes.model;

import static us.fatehi.utility.Utility.isBlank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import schemacrawler.schemacrawler.SchemaCrawlerException;

public abstract class ObjectAttributes implements Serializable, Comparable<ObjectAttributes> {

  private static final long serialVersionUID = -6819484903391182146L;

  private final String name;
  private final List<String> remarks;
  private final Map<String, String> attributes;

  public ObjectAttributes(
      final String name, final List<String> remarks, final Map<String, String> attributes)
      throws SchemaCrawlerException {

    if (isBlank(name)) {
      throw new SchemaCrawlerException("No name provided");
    }
    this.name = name;

    if (remarks == null) {
      this.remarks = Collections.emptyList();
    } else {
      this.remarks = new ArrayList<>(remarks);
    }
    if (attributes == null) {
      this.attributes = Collections.emptyMap();
    } else {
      this.attributes = new TreeMap<>(attributes);
    }
  }

  @Override
  public int compareTo(final ObjectAttributes o) {
    if (o == null) {
      return 1;
    }
    return name.compareTo(o.name);
  }

  public Map<String, String> getAttributes() {
    return attributes;
  }

  public String getName() {
    return name;
  }

  public String getRemarks() {
    return String.join(System.lineSeparator(), remarks);
  }

  public boolean hasAttributes() {
    return !attributes.isEmpty();
  }

  public boolean hasRemarks() {
    if (remarks.isEmpty()) {
      return false;
    } else {
      return !isBlank(getRemarks());
    }
  }
}

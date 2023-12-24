/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;
import java.util.Objects;

public class BaseProductVersion implements ProductVersion {

  private static final long serialVersionUID = 4051323422934251828L;

  private final String productName;
  private final String productVersion;

  public BaseProductVersion(final ProductVersion productVersion) {
    this(
        requireNonNull(productVersion, "No product name provided").getProductName(),
        productVersion.getProductVersion());
  }

  public BaseProductVersion(final String productName, final String productVersion) {
    this.productName = requireNotBlank(productName, "No product name provided");
    this.productVersion = requireNotBlank(productVersion, "No product version provided");
  }

  @Override
  public final boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || !(obj instanceof BaseProductVersion)) {
      return false;
    }
    final ProductVersion other = (ProductVersion) obj;
    return Objects.equals(productName, other.getProductName())
        && Objects.equals(productVersion, other.getProductVersion());
  }

  @Override
  public String getProductName() {
    return productName;
  }

  @Override
  public String getProductVersion() {
    return productVersion;
  }

  @Override
  public final int hashCode() {
    return Objects.hash(productName, productVersion);
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return String.format("%s %s", productName, productVersion);
  }
}

/*
========================================================================
OperatingSystem
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

OperatingSystem is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

OperatingSystem and the accompanying materials are made available under
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


import static java.util.Objects.requireNonNull;

import schemacrawler.schema.ProductVersion;

/**
 * Operating system information.
 *
 * @author Sualeh Fatehi sualeh@hotmail.com
 */
class BaseProductVersion
  implements ProductVersion
{

  private static final long serialVersionUID = 4051323422934251828L;

  private final String productName;
  private final String productVersion;

  protected BaseProductVersion(final ProductVersion productVersion)
  {
    this(requireNonNull(productVersion, "No product name provided")
      .getProductName(), productVersion.getProductVersion());
  }

  protected BaseProductVersion(final String productName,
                               final String productVersion)
  {
    this.productName = requireNonNull(productName, "No product name provided");
    this.productVersion = requireNonNull(productVersion,
                                         "No product version provided");
  }

  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (!(obj instanceof BaseProductVersion))
    {
      return false;
    }
    final BaseProductVersion other = (BaseProductVersion) obj;
    if (productName == null)
    {
      if (other.productName != null)
      {
        return false;
      }
    }
    else if (!productName.equals(other.productName))
    {
      return false;
    }
    if (productVersion == null)
    {
      if (other.productVersion != null)
      {
        return false;
      }
    }
    else if (!productVersion.equals(other.productVersion))
    {
      return false;
    }
    return true;
  }

  @Override
  public String getProductName()
  {
    return productName;
  }

  @Override
  public String getProductVersion()
  {
    return productVersion;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (productName == null? 0: productName.hashCode());
    result = prime * result
             + (productVersion == null? 0: productVersion.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return String.format("%s %s", productName, productVersion);
  }

}

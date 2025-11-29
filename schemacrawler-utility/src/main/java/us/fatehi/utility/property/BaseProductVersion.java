/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.property;

import static java.util.Objects.requireNonNull;

import java.io.Serial;

public class BaseProductVersion extends AbstractProperty implements ProductVersion {

  @Serial private static final long serialVersionUID = 4051323422934251828L;

  public BaseProductVersion(final ProductVersion productVersion) {
    this(
        requireNonNull(productVersion, "No product name provided").getProductName(),
        productVersion.getProductVersion());
  }

  public BaseProductVersion(final String productName, final String productVersion) {
    super(new PropertyName(productName), productVersion);
  }

  @Override
  public String toString() {
    return getName() + " " + getValue();
  }
}

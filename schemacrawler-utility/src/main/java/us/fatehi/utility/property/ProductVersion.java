/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package us.fatehi.utility.property;

public interface ProductVersion extends Property {

  /**
   * Gets the name of the product.
   *
   * @return Name of the product
   */
  default String getProductName() {
    return getName();
  }

  /**
   * Gets the version of the product.
   *
   * @return Version of the product
   */
  default String getProductVersion() {
    return String.valueOf(getValue());
  }
}

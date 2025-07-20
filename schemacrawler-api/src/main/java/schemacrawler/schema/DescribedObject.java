/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

public interface DescribedObject {

  /**
   * Getter for remarks.
   *
   * @return Remarks
   */
  String getRemarks();

  /**
   * Whether remarks are available.
   *
   * @return Remarks
   */
  boolean hasRemarks();

  /**
   * Setter for remarks.
   *
   * @param remarks Remarks to set
   */
  void setRemarks(final String remarks);
}

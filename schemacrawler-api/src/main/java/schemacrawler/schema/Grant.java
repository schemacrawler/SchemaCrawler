/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

import java.io.Serializable;

public interface Grant<D extends DatabaseObject>
    extends Serializable, Comparable<Grant<D>>, ContainedObject<Privilege<D>> {

  /**
   * Gets the grantee.
   *
   * @return Grantee
   */
  String getGrantee();

  /**
   * Gets the grantor.
   *
   * @return Grantor
   */
  String getGrantor();

  /**
   * If the privilege is grantable.
   *
   * @return Is grantable
   */
  boolean isGrantable();
}

/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.property;

/** Operating system information. */
public final class OperatingSystemInfo extends BaseProductVersion {

  private static final long serialVersionUID = 4051323422934251828L;

  private static final OperatingSystemInfo OPERATING_SYSTEM_INFO = new OperatingSystemInfo();

  public static OperatingSystemInfo operatingSystemInfo() {
    return OPERATING_SYSTEM_INFO;
  }

  private OperatingSystemInfo() {
    super(System.getProperty("os.name", "<unknown>"), System.getProperty("os.version", ""));
  }
}
